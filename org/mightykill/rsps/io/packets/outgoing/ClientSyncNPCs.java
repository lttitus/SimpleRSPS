package org.mightykill.rsps.io.packets.outgoing;

import java.util.ArrayList;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Hit;
import org.mightykill.rsps.entities.movement.Movement;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.skills.Skill;
import org.mightykill.rsps.io.packets.BitPacker;
import org.mightykill.rsps.util.Misc;

public class ClientSyncNPCs extends OutgoingPacket {
	
	public ClientSyncNPCs(Player p) {
		super(222, 0, true, true);
		
		BitPacker pack = new BitPacker();
		RawPacket updateBlock = new RawPacket();
		Position pPos = p.getPosition();
		
		//Update known NPCs first
		ArrayList<NPC> knownNPCs = p.getSeenNPCs();
		pack.addBits(8, knownNPCs.size());
		for(NPC other:knownNPCs) {	//Max surrounding NPC count is capped at 255 (8 bits sent above)
			if(other != null) {
				Movement oMove = other.getMovement();
				boolean withinDistance = Misc.withinDistance(p, other);
				
				if(other.appearanceUpdated ||	//An update occurred
					oMove.walkDir != -1    ||
					oMove.runDir != -1     ||
					oMove.teleported ||
					!withinDistance) {	//No longer within distance, need to be removed
					pack.addBits(1, 1);
					
					if(withinDistance && !other.isHidden()) {
						if(oMove.walkDir != -1) {
							if(oMove.runDir != -1) {	//We covered 2 tiles this tick (we did a run)
								pack.addBits(2, 2);
								
								pack.addBits(3, oMove.walkDir);
								pack.addBits(3, oMove.runDir);
								pack.addBits(1, other.appearanceUpdated?1:0);
							}else {	//Only walking one tile
								pack.addBits(2, 1);
								
								pack.addBits(3, oMove.walkDir);
								pack.addBits(1, other.appearanceUpdated?1:0);
							}
						}else {	//No movement update
							pack.addBits(2, 0);
						}
					}else {	//Remove this NPC from the client's update loop
						pack.addBits(2, 3);
						p.forgetNPC(other);
					}
				}else {	//No update at all
					pack.addBits(1, 0);
				}
			}	//Continue on, as there is not a NPC at this position
		}
		
		//Add new NPC(s) to the client's update loop
		ArrayList<NPC> localNPCs = Engine.npcs.getNPCsInArea(p);
		for(NPC localNPC:localNPCs) {
			boolean known = knownNPCs.contains(localNPC);
			
			if(!known && !localNPC.isHidden() && p.seeNPC(localNPC)) {	//If we don't know the NPC and we can see them (not too many people around us), add them to the Client and the Server Entity
				pack.addBits(15, localNPC.getWorldIndex());
				pack.addBits(14, localNPC.getNPCId());
				pack.addBits(1, localNPC.appearanceUpdated?1:0);
				
				Position newNPCPos = localNPC.getPosition();
				int xDiff = newNPCPos.x - pPos.x;
				if(xDiff < 0) xDiff += 32;
				int yDiff = newNPCPos.y - pPos.y;
				if(yDiff < 0) yDiff += 32;
				
				pack.addBits(5, yDiff);
				pack.addBits(5, xDiff);
				pack.addBits(3, localNPC.getMovement().getCurrentDirection());
				pack.addBits(1, 1);
				
				
				knownNPCs.add(localNPC);
			}	//If we found the NPC in the list, that means we know of them already; ignore them
		}
		
		for(NPC knownNPC:knownNPCs) {
			if(knownNPC.appearanceUpdated && !knownNPC.isHidden()) {
				appendUpdateBlock(knownNPC, updateBlock);
			}
		}
		
		/* Build the packet */
		byte[] updateData = updateBlock.getData();
		if(updateData.length >= 3) pack.addBits(15, 32767);
		byte[] mainData = pack.getBytes();
		
		/* Bring it all together */
		addBytes(mainData);
		if(updateData.length > 0) {	
			addBytes(updateData);
		}
	}
	
	private void appendUpdateBlock(NPC n, OutgoingPacket pack) {
		int updateMask = 0;
		
		ArrayList<Hit> damage = n.getCombat().getDamageThisTick();
		if(damage.size() > 1) updateMask |= 0x20;
		//if(p.appearanceUpdated) updateMask |= 0x80;
		//if(n.updateFaceCoords) updateMask |= 0x40;	//0x40
		if(n.updateFaceEntity) updateMask |= 0x10;
		//if(updateMask > 0x100) updateMask |= 0x10;	//Any flags > 0x100 should be added above this line to be written correctly. Otherwise, they will be ignored
		//if(p.chatMessage != null) updateMask |= 0x8;	//TODO: Implement this
		//if(thisUpdate.forceChat != null) updateMask |= 0x4;	//TODO: Implement this
		if(damage.size() >= 1) updateMask |= 0x4;
		if(n.updateAnimation) updateMask |= 0x1;	//TODO: Fix this?
		pack.addByte(updateMask);	//Write the mask
		//System.out.println("Update mask: "+updateMask);
		
		//if(thisUpdate.forceChat != null) forceChat(p, pack);
		//if(p.chatMessage != null) sendChat(p, pack);
		//if(p.appearanceUpdated) appendPlayerAppearance(p, pack);
		//if(n.updateFaceCoords) sendFaceCoords(n.facingCoords, pack);
		if(n.updateFaceEntity) sendFaceTo(n.facingEntity, pack);
		if(n.updateAnimation) sendAnimation(n.currentAnimation, pack);
		if(damage.size() > 1) sendHit2(damage.get(1), pack);
		if(damage.size() >= 1) sendHit1(n, damage.get(0), pack);
	}
	
	private void sendHit1(NPC n, Hit hit, OutgoingPacket pack) {
		pack.addByte(hit.damage);
		pack.addByte(hit.type);
		
		int hpRatio = (n.getSkillLevel(Skill.HITPOINTS)*255 / n.getInitialSkillLevel(Skill.HITPOINTS));
        pack.addByteS(hpRatio>255?255:hpRatio);
	}
	
	private void sendHit2(Hit hit, OutgoingPacket pack) {
		pack.addByte(hit.damage);
		pack.addByteS(hit.type);
	}
	
	private void sendFaceTo(Entity e, OutgoingPacket pack) {
		int id = -1;
		if(e != null) {
			id = e.getWorldIndex();
			if(e instanceof NPC) {
				id |= 0x8000;
			}
		}
		pack.addShort(id);
	}
	
	private void sendAnimation(int animId, OutgoingPacket pack) {
		pack.addShortA(animId);
        pack.addByte(0);
	}

}
