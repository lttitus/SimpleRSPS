package org.mightykill.rsps.io.packets.outgoing;

import java.awt.Point;
import java.util.ArrayList;
import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Hit;
import org.mightykill.rsps.entities.movement.Movement;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.BitPacker;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.util.Misc;

/**
 * Send a packet to the Client, informing it of required Player Updates.<br>
 * This should be sent every tick after processing has occurred.<br>
 * <br>
 * https://www.rune-server.ee/runescape-development/rs2-server/informative-threads/125681-player-updating-procedure.html
 * @author Green
 */
public class ClientSyncPlayers extends OutgoingPacket {
	
	private Player origin;

	public ClientSyncPlayers(Player p) {
		super(216, 0, true, true);
		this.origin = p;
		
		BitPacker pack = new BitPacker();
		RawPacket updateBlock = new RawPacket();
	
		/* Local Player Movement */
		Movement pMove = p.getMovement();
		Position pPos = pMove.getPosition();
		Point localPos = pPos.getLocalPosition();

		if(pMove.walkDir != -1 ||
			pMove.runDir != -1 ||
			p.appearanceUpdated ||
			pMove.teleported) {
			pack.addBits(1, 1);
			
			if(pMove.teleported) {	//We teleported
				pack.addBits(2, 3);
				
				pack.addBits(7, localPos.x);
				pack.addBits(1, 1);
				pack.addBits(2, pPos.h);
				pack.addBits(1, p.appearanceUpdated?1:0);
				pack.addBits(7, localPos.y);
			}else {
				if(pMove.walkDir != -1) {
					if(pMove.runDir != -1) {
						pack.addBits(2, 2);
					
						pack.addBits(3, pMove.walkDir);
						pack.addBits(3, pMove.runDir);
						pack.addBits(1, p.appearanceUpdated?1:0);
					}else {	//Walking
						pack.addBits(2, 1);
						
						pack.addBits(3, pMove.walkDir);
						pack.addBits(1, p.appearanceUpdated?1:0);
					}
				}else {	//No movement, but maybe appearance changed
					pack.addBits(2, 0);
				}
			}
		}else {	//No update required
			pack.addBits(1, 0);
		}
		
		//Local Client Block Updates
		if(p.appearanceUpdated) appendUpdateBlock(p, updateBlock, false);
		
		//Update known players first
		ArrayList<Player> knownPlayers = p.getSeenPlayers();
		ArrayList<Player> newPlayers = new ArrayList<Player>();
		pack.addBits(8, knownPlayers.size());
		for(Player other:knownPlayers) {	//Max surrounding player count is capped at 255 (8 bits sent above)
			if(other != null) {
				Movement oMove = other.getMovement();
				boolean withinDistance = Misc.withinDistance(p, other);
				
				if(other.appearanceUpdated ||	//An update occurred
					oMove.walkDir != -1    ||
					oMove.runDir != -1     ||
					oMove.teleported ||
					!withinDistance ||		//No longer within distance
					!other.isConnected()) {	//No longer connected
					pack.addBits(1, 1);
					
					if(withinDistance && other.isConnected() && !other.isHidden()) {
						if(oMove.walkDir != -1) {
							if(oMove.runDir != -1) {	//We covered 2 tiles this tick (running)
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
					}else {	//Remove this Player from the client's update loop
						pack.addBits(2, 3);
						p.forgetPlayer(other);
					}
				}else {	//No update at all
					pack.addBits(1, 0);
				}
			}	//Continue on, as there is not a Player at this position
		}
		
		//Add new player(s) to the client's update loop
		ArrayList<Player> localPlayers = Engine.players.getPlayersInArea(p);
		localPlayers.remove(p);
		for(Player localPlayer:localPlayers) {
			boolean known = knownPlayers.contains(localPlayer);
			
			if(!known && !localPlayer.isHidden() && p.seePlayer(localPlayer)) {	//If we don't know the Player and we can see them (not too many people around us), add them to the Client and the Server Entity
				pack.addBits(11, localPlayer.getWorldIndex());
				
				Position newPlayerPos = localPlayer.getPosition();
				int xDiff = newPlayerPos.x - pPos.x;
				if(xDiff < 0) xDiff += 32;
				int yDiff = newPlayerPos.y - pPos.y;
				if(yDiff < 0) yDiff += 32;
				
				pack.addBits(5, xDiff);
				pack.addBits(1, 1);
				pack.addBits(3, localPlayer.getMovement().getCurrentDirection());
				pack.addBits(1, 1);
				pack.addBits(5, yDiff);
				
				newPlayers.add(localPlayer);
			}	//If we found the Player in the list, that means we know of them already; ignore them
		}
		
		for(Player knownPlayer:knownPlayers) {
			if(knownPlayer.appearanceUpdated && !knownPlayer.isHidden()) {
				appendUpdateBlock(knownPlayer, updateBlock, false);
			}
		}
		
		for(Player newPlayer:newPlayers) {
			appendUpdateBlock(newPlayer, updateBlock, true);
		}
		
		/* Build the packet */
		byte[] updateData = updateBlock.getData();
		if(updateData.length > 0) pack.addBits(11, 2047);
		byte[] mainData = pack.getBytes();
		
		/* Bring it all together */
		addBytes(mainData);
		if(updateData.length > 0) {	
			addBytes(updateData);
		}
	}
	
	private void appendUpdateBlock(Player p, OutgoingPacket pack, boolean forceUpdate) {
		int updateMask = 0;
		
		ArrayList<Hit> damage = p.getCombat().getDamageThisTick();
		if(damage.size() > 1) updateMask |= 0x200;
		if(p.appearanceUpdated || forceUpdate) updateMask |= 0x80;
		if(p.updateFaceCoords) updateMask |= 0x40;
		if(p.updateFaceEntity) updateMask |= 0x20;
		if(updateMask >= 0x100) updateMask |= 0x10;	//Any flags > 0x100 should be added above this line to be written correctly. Otherwise, they will be ignored
		if(p.chatMessage != null) updateMask |= 0x8;
		//if(thisUpdate.forceChat != null) updateMask |= 0x4;	//TODO: Implement this
		if(damage.size() >= 1) updateMask |= 0x2;
		if(p.updateAnimation) updateMask |= 0x1;
		if((0x10 & updateMask) == 0x10) pack.addShortBigEndian(updateMask); else pack.addByte(updateMask);	//Write the mask
		
		//if(thisUpdate.forceChat != null) forceChat(p, pack);
		if(damage.size() > 1) sendHit2(damage.get(1), pack);
		if(p.updateFaceCoords) sendFaceCoords(p.facingCoords, pack);
		if(p.updateFaceEntity) sendFaceEntity(p.facingEntity, pack);
		if(p.chatMessage != null) sendChat(p, pack);
		if(p.updateAnimation) sendAnimation(p.currentAnimation, pack);
		if(p.appearanceUpdated || forceUpdate) appendPlayerAppearance(p, pack);
		if(damage.size() >= 1) sendHit1(p, damage.get(0), pack); 
	}
	
	private void sendHit1(Player p, Hit hit, OutgoingPacket pack) {
		pack.addByteS(hit.damage);
		pack.addByteS(hit.type);
		
		int hpRatio = (p.getSkillLevel(3)*255 / p.getLevelForXP(3));
        pack.addByteS(hpRatio>255?255:hpRatio);
	}
	
	private void sendHit2(Hit hit, OutgoingPacket pack) {
		pack.addByteS(hit.damage);
		pack.addByteA(hit.type);
	}
	
	private void sendFaceCoords(Point coords, OutgoingPacket pack) {
		if(coords == null) return;
		int xOffs = coords.x;
		int yOffs = coords.y;
		pack.addShort(xOffs);
		pack.addShortBigEndianA(yOffs);
	}
	
	private void sendFaceEntity(Entity facing, OutgoingPacket pack) {
		int id = -1;
		if(facing != null) {
			id = facing.getWorldIndex();
			if(facing instanceof NPC) {
				id |= 0x8000;
			}
		}
		
		pack.addShort(id);
	}
	
	/*private void forceChat(Player p, OutgoingPacket pack) {
		addString(p.currentUpdate.forceChat);
	}*/
	
	private void sendChat(Player p, OutgoingPacket pack) {
		byte[] chat = new byte[256];
		chat[0] = (byte)p.chatMessage.length();
		int offset = 1+Misc.encryptPlayerChat(chat, 0, 1, p.chatMessage.length(), p.chatMessage.getBytes());
		
		pack.addShortA(p.chatMessageEffects);
		pack.addByteC(p.getRights());
		pack.addByteC(offset);
		pack.addBytes(chat, 0, offset);
	}
	
	private void sendAnimation(int animId, OutgoingPacket pack) {
		pack.addShort(animId);
		pack.addByteS(0);	//No delay
	}
	
	private static final int[] BODY_MAP = new int[] {2, -1, 3, 5, 0, 4, 6, 1};	//pLook map
	private void appendPlayerAppearance(Player p, OutgoingPacket pack) {
		RawPacket block = new RawPacket();
		
		block.addByte(p.gender);	//Gender
		if ((p.gender & 0x2) == 2) {
            block.addByte(0);
            block.addByte(0);
        }
		block.addByte(p.skullIcon);	//Skull - -1 = off, 0 = on, 1 = red, 2 = 5vt - 6 = 1vt, 7 = :O
		block.addByte(-1);	//Prayer - -1 = off, 0 = pfmelee, 1 = pfmissles, 2 = pfmagic, 3 = retribution, 4 = smite, 5 = Redemption, 6 = pfmagic+missles, 7 = pfsummon, 8 = pfmelee+summon, 9 = pfmissles+summon, 10 = pfmagic+summon, 11 = blank??

		if(p.pnpc == -1) {
		
			/* Accessories */
			for(int accessorySlot=0;accessorySlot<4;accessorySlot++) {
				Item item = p.getEquipment().getItemInSlot(accessorySlot);
				
				if(item != null) {
					int equipId = Engine.items.getEquipment(item.getItemId()).getEquipId();
					
					block.addShort(0x8000 | equipId);
				}else {
					block.addByte(0);
				}
			}
			
			/* Equipment */
			for(int equipmentSlot=4;equipmentSlot<12;equipmentSlot++) {
				Item item = null;
				
				if(equipmentSlot == 6) {		//Arms (Torso)
					item = p.getEquipment().getItemInSlot(4);
				}else if(equipmentSlot == 8) {	//Hair (Head)
					item = p.getEquipment().getItemInSlot(0);
				}else if(equipmentSlot == 11) {	//Beard(Head)
					item = p.getEquipment().getItemInSlot(0);
				}else {
					item = p.getEquipment().getItemInSlot(equipmentSlot);
				}
				
				if(equipmentSlot != 5) {
					int pLook = p.pLook[BODY_MAP[equipmentSlot-4]];
					int covered = item != null?Engine.items.getEquipment(item.getItemId()).getCovering():0;
					
					if(equipmentSlot == 6 || equipmentSlot == 11) {	//Arms or Beard; Secondary hidden appearance elements
						if((covered & 0x2) == 2) {	//Hidden
							block.addByte(0);
						}else {
							block.addShort(0x100 | pLook);
						}
					}else {	//Normal equipment; Primary hidden appearance elements
						if(item == null) {
							if((covered & 0x1) == 1) {	//Hidden
								block.addByte(0);
							}else {
								block.addShort(0x100 | pLook);
							}
						}else {
							int equipId = Engine.items.getEquipment(item.getItemId()).getEquipId();
							
							block.addShort(0x8000 | equipId);
						}
					}
				}else {	//Shield
					if(item != null) {
						block.addShort(0x8000 | item.getItemId());
					}else {
						block.addByte(0);
					}
				}
			}
		
		}else {
			block.addShort(-1);
			block.addShort(p.pnpc);
		}
		
		/* Colors */
		for(int color:p.color) {
			block.addByte(color);
		}
		
		/* Animations */
		block.addShort(p.getIdleAnimation());	//Stand
		block.addShort(0x337);	//Other
		block.addShort(p.getWalkAnimation());	//Walk
		block.addShort(0x334);	//Other
		block.addShort(0x335);	//Other
		block.addShort(0x336);	//Other
		block.addShort(0x338);	//Run
		
		/* Miscellaneous Player information */
		block.addLong(Misc.stringToLong(p.getName()));
		block.addByte(p.getCombatLevel());
		block.addShort(0);	//Skill-level
		
		byte[] blockData = block.getData();
		pack.addByte(blockData.length);
		for(byte b:blockData) {
			pack.addByte(b);
		}
	}

}
