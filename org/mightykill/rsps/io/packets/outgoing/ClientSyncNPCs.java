package org.mightykill.rsps.io.packets.outgoing;

import java.awt.Point;
import java.util.ArrayList;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Hit;
import org.mightykill.rsps.entities.movement.Movement;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.npc.NPCManager;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.skills.Skill;
import org.mightykill.rsps.io.packets.BitPacker;
import org.mightykill.rsps.util.Misc;
import org.mightykill.rsps.world.regions.Region;

public class ClientSyncNPCs extends OutgoingPacket {
	
	public ClientSyncNPCs(Player p) {
		super(222, 0, true, true);
		
		BitPacker pack = new BitPacker();
		RawPacket updateBlock = new RawPacket();
		
		/* External Player Movement updates */
		ArrayList<NPC> localNPCs = Engine.npcs.getNPCsInArea(p);//Engine.regions.getNPCsInArea(regionPoint.x, regionPoint.y);	//All nearby NPCs, within a 3x3 Region area
		ArrayList<NPC> difference = Misc.getRemovingNPCs(p.localNPCs, localNPCs);	//List players that were previously updated, but no longer will be. Disconnected, teleported, moved out of region, etc...
		p.localNPCs = (ArrayList<NPC>) localNPCs.clone();
		localNPCs.addAll(difference);
		
		ArrayList<NPC> seenNPCList = new ArrayList<NPC>();		//We have updated these players before
		ArrayList<NPC> unseenNPCList = new ArrayList<NPC>();	//Client is not aware of these players yet
		
		for(NPC n:localNPCs) {
			if(difference.contains(n) || n.getMovement().teleported) continue;	//Ignore Players that are being removed
			int id = n.getWorldIndex();
			if(!p.seenNPC[id]) {
				unseenNPCList.add(n);
				p.seenNPC[id] = true;	//"See" them for the next iteration
				n.appearanceUpdated = true;
			}else {
				seenNPCList.add(n);
			}
		}
		p.debug("Previous Local: "+p.localNPCs.size()+"; New Local: "+localNPCs.size()+"; Difference: "+difference.size()+"; Seen: "+seenNPCList.size()+"; Unseen: "+unseenNPCList.size());
		
		/* Update known players */
		pack.addBits(8, seenNPCList.size());
		for(NPC n:seenNPCList) {
			Movement nMove = n.getMovement();
			boolean removed = difference.contains(n);
			if(n.appearanceUpdated  ||
				nMove.walkDir != -1 ||
				nMove.runDir != -1  ||
				nMove.teleported    ||
				removed) {
				pack.addBits(1, 1);
				
				if(!removed) {
					if(nMove.walkDir != -1) {
						if(nMove.runDir != -1) {	//Running
							pack.addBits(2, 2);

							pack.addBits(3, nMove.walkDir);
							pack.addBits(3, nMove.runDir);
							pack.addBits(1, n.appearanceUpdated?1:0);
						}else {	//Walking
							pack.addBits(2, 1);
							
							pack.addBits(3, nMove.walkDir);
							pack.addBits(1, n.appearanceUpdated?1:0);
						}
					}else {	//No movement change
						pack.addBits(2, 0);
					}
				}else {	//Remove from client's update list
					p.debug("Removing "+n.getName()+" "+n.getWorldIndex());
					pack.addBits(2, 3);
				}
			}else {	//No update at all
				pack.addBits(1, 0);
			}
		}
		
		/* Add New NPC to Client Update List */
		for(NPC n:unseenNPCList) {
			pack.addBits(15, n.getWorldIndex());
			pack.addBits(14, n.getNPCId());
			pack.addBits(1, n.appearanceUpdated?1:0);
			
			int yPos = n.getPosition().y - p.getPosition().y;
	        if (yPos < 0) yPos += 32;
	        int xPos = n.getPosition().x - p.getPosition().x;
	        if (xPos < 0) xPos += 32;
	        
	        pack.addBits(5, yPos);
			pack.addBits(5, xPos);
			pack.addBits(3, n.getMovement().getCurrentDirection());
			pack.addBits(1, 1);
		}
		
		/* Update required external players */
		for(NPC n:localNPCs) {
			if(difference.contains(n) || n.getMovement().teleported) {	//If we removed them, 'un-see' that player so they may be added later
				int id = n.getWorldIndex();
				//p.localNPCs.remove(n);
				p.seenNPC[id] = false;
				continue;
			}
			
			if(n.appearanceUpdated) appendUpdateBlock(n, updateBlock);
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
			id = e.getWorldIndex()+32768;
		}
		pack.addShort(id);
	}
	
	private void sendAnimation(int animId, OutgoingPacket pack) {
		pack.addShortA(animId);
        pack.addByte(0);
	}

}
