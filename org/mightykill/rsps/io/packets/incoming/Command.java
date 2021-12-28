package org.mightykill.rsps.io.packets.incoming;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.actions.PoisonAction;
import org.mightykill.rsps.entities.movement.Movement;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.skills.Skill;
import org.mightykill.rsps.exchange.offers.BuyOffer;
import org.mightykill.rsps.exchange.offers.SellOffer;
import org.mightykill.rsps.io.packets.outgoing.CreateGroundItem;
import org.mightykill.rsps.io.packets.outgoing.PlayMusic;
import org.mightykill.rsps.io.packets.outgoing.PlaySound;
import org.mightykill.rsps.io.packets.outgoing.SendInterfaceConfig;
import org.mightykill.rsps.io.packets.outgoing.ShowInterface;
import org.mightykill.rsps.io.packets.outgoing.TestPacket;
import org.mightykill.rsps.io.packets.outgoing.UpdateGEOffer;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.world.regions.Region;
import org.mightykill.rsps.world.zones.Zone;

public class Command extends IncomingPacket {
	
	private Player p;

	public Command(int packetSize, byte[] data, Player origin) {
		super(107, packetSize, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		String rawCmd = nextString();
		String[] cmd = rawCmd.split(" ");
		
		switch(cmd[0]) {
			
		case "pnpc":
			try {
				int npcId = Integer.parseInt(cmd[1]);
				
				if(npcId > -1) {
					p.pnpc = npcId;
					p.appearanceUpdated = true;
					p.sendMessage("Morphing...");
				}else {
					p.sendMessage("<col=ab0000>Invalid NPC Id!");
				}
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::pnpc [npcid]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				if(p.pnpc == -1) {
					p.sendMessage("<col=ab0000>Invalid format! Should be ::pnpc [npcid]");
				}else {
					p.pnpc = -1;
					p.appearanceUpdated = true;
					p.sendMessage("Un-morphing...");
				}
			}
			break;
			
		case "gi":
			try {
				int itemId = Integer.parseInt(cmd[1]);
				int itemAmt = Integer.parseInt(cmd[2]);
				
				if(itemId > -1) {
					Engine.groundItems.createGroundItem(new Item(itemId, itemAmt), p.getPosition().x, p.getPosition().y, p);
					//p.dropItem(itemId, itemAmt);
				}else {
					p.sendMessage("<col=ab0000>Invalid NPC Id!");
				}
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			}
			break;
			
		case "npc":
			if(p.getRights() >= 2) {
				try {
					int npcId = Integer.parseInt(cmd[1]);
					
					if(npcId > -1) {
						if(!Engine.npcs.spawnNPC(npcId, p.getPosition())) {
							p.sendMessage("<col=ab0000>Unable to spawn this NPC for some reason!");
						}
					}else {
						p.sendMessage("<col=ab0000>Invalid NPC Id!");
					}
				
				} catch(NumberFormatException nfe) {
					p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
				} catch(ArrayIndexOutOfBoundsException oobe) {
					p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
				}
			}
			break;
			
		case "reloadnpcs":
			p.sendMessage("Reloaded "+Engine.npcs.loadNPCDefinitions()+" NPC Definitions");
			p.sendMessage("Respawning "+Engine.npcs.loadNPCs()+" NPCs");
			for(Player p:Engine.players.getPlayerList()) {
				//p.localNPCs = .clear();
			}
			break;
			
		case "debug":
			if(p.getRights() >= 2) {
				p.toggleDebug();
				p.sendMessage("Debugging is now "+(p.isDebug()?"on":"off"));
			}
			break;
			
		case "levelup":
			for(int i=0;i<24;i++) {
				p.setLevelByXP(i, 13141200);
			}
			break;
			
		case "noob":
			for(int i=0;i<24;i++) {
				p.setLevelByXP(i, 0);
			}
			p.setLevelByXP(Skill.HITPOINTS, 1171);
			break;
			
		case "item":
			try {
				int itemId = Integer.parseInt(cmd[1]);
				int amount = Integer.parseInt(cmd[2]);
				
				p.giveItem(itemId, amount);
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::item [itemid] [amount]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::item [itemid] [amount]");
			}
			break;
			
		case "poison":
			p.sendMessage("You poisoned yourself! Why..?");
			p.queueAction(new PoisonAction(null, p, Engine.getTick(), 6));
			break;
			
		case "config":
			try {
				int confId = Integer.parseInt(cmd[1]);
				int set = Integer.parseInt(cmd[2]);
				
				p.confTest = confId;
				p.sendConfig(confId, set);
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::config [confid] [set]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::config [confid] [set]");
			}
			break;
			
		case "blink":
			p.blinkSkillIcon(1);
			break;
			
		case "sound":
			try {
				int soundId = Integer.parseInt(cmd[1]);
				
				p.testSound = soundId;
				p.playSound(soundId);
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::sound [confid]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::sound [confid]");
			}
			break;
			
		case "music":
			try {
				int musicId = Integer.parseInt(cmd[1]);
				
				p.sendPacket(new PlayMusic(musicId));
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::music [id]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::music [id]");
			}
			break;
			
		case "if":
			try {
				int interfaceId = Integer.parseInt(cmd[1]);
				
				p.sendPacket(new ShowInterface(interfaceId, 8, 548, 0));
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::if [id]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::if [id]");
			}
			break;
			
		case "tp":
			try {
				int x = Integer.parseInt(cmd[1]);
				int y = Integer.parseInt(cmd[2]);
				
				p.teleport(x, y);
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			}
			break;
			
		case "ge":
			try {
				p.getMovement().teleport(new Position(3164, 3487, 0));
				/*int a = Integer.parseInt(cmd[1]);
				int b = Integer.parseInt(cmd[2]);
				int c = Integer.parseInt(cmd[3]);
				int d = Integer.parseInt(cmd[4]);
				int e = Integer.parseInt(cmd[5]);
				int f = Integer.parseInt(cmd[6]);
				int g = Integer.parseInt(cmd[7]);
				
				p.sendPacket(new UpdateGEOffer(a, b, c, d, e, f, g));*/
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			}
			break;
			
		case "pos":
			Position pos = p.getPosition();
			p.sendMessage("Current Position: "+pos.x+", "+pos.y+", "+pos.h);
			
			break;
			
		/*case "reg":
			Region r = p.getRegion();
			p.sendMessage("Region "+r.getId()+": "+r.getX()+", "+r.getY()+"; "+r.getDescription()+"; musicId: "+r.getMusicId());
			break;*/
			
		/*case "loc":
			Position pPos = p.getPosition();
			Point localPos = pPos.getLocalPosition();
			Point regPoint = pPos.getMapRegion();
			
			p.sendMessage(pPos.x+", "+pPos.y+"; "+localPos.x+", "+localPos.y+"; "+regPoint.x+", "+regPoint.y);
			break;*/
			
		case "reloadregions":
			p.sendMessage("Loaded "+Engine.regions.loadRegions()+" regions.");
			break;
			
		case "tele":
			if(p.getRights() >= 2) {
				try {
					String[] part = cmd[1].split(",");
					
					int h = Integer.parseInt(part[0]);
					int sectx = Integer.parseInt(part[1]);
					int secty = Integer.parseInt(part[2]);
					int addx = Integer.parseInt(part[3]);
					int addy = Integer.parseInt(part[4]);
					int newx = ((sectx*8) << 3)+addx;
					int newy = ((secty*8) << 3)+addy;
					p.sendMessage(newx+", "+newy);
					
					p.teleport(newx, newy, h);
					//p.getMovement().teleport(new Position(newx, newy, h)/*, p*/);
				
				} catch(NumberFormatException nfe) {
					p.sendMessage("<col=ab0000>Invalid format! Should be ::tele [sectx],[secty],[absx],[absy]");
				} catch(ArrayIndexOutOfBoundsException oobe) {
					p.sendMessage("<col=ab0000>Invalid format! Should be ::tele [sectx],[secty],[absx],[absy]");
				}
			}
			break;
			
		case "save":
			p.sendMessage(""+Engine.players.savePlayer(p));
			break;
			
		case "clearcache":
			Engine.items.clearCache();
			p.sendMessage("Cleared item cache");
			break;
			
		case "allconfig":
			for(int i=0;i<2000;i++) {
				p.sendConfig(i, i);
			}
			break;
			
		case "img":
			try {
				int img = Integer.parseInt(cmd[1]);
				
				p.sendMessage("<img="+img+">");
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::img [id]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::img [id]");
			}
			
			break;
			
		/*case "reloadnpcs":
			p.sendMessage("Loaded "+Engine.npcs.loadNPCs()+" npcs.");
			break;
			*/
		case "reloadzones":
			p.sendMessage("Loaded "+Engine.zones.loadZones()+" zones.");
			break;
			
		case "zones":
			ArrayList<Zone> zones = p.getZones();
			if(zones.size() > 0) {
				for(Zone z:zones) {
					Rectangle bounds = z.getBounds();
					StringBuilder sb = new StringBuilder();
					if(z.isMulti()) sb.append("multi ");
					if(z.isPvp()) sb.append("pvp ");
					String mods = sb.toString();
					p.sendMessage("Zone "+z.getZoneId()+": "+bounds.x+", "+bounds.y+" - "+bounds.width+", "+bounds.height+"; "+(!mods.equals("")?mods:"none"));
				}
			}else {
				p.sendMessage("You are not in any zones.");
			}
			break;
			
		case "anim":
			try {
				int anim = Integer.parseInt(cmd[1]);
				
				p.setAnimation(anim, 0);
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::anim [id]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::anim [id]");
			}
			break;
			
		case "ifconfig":
			try {
				int iface = Integer.parseInt(cmd[1]);
				int child = Integer.parseInt(cmd[2]);
				int set = Integer.parseInt(cmd[3]);
				
				p.sendPacket(new SendInterfaceConfig(iface, child, set));
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::npc [npcid]");
			}
			break;
			
		case "tryconf":
			try {
				int interfaceid = Integer.parseInt(cmd[1]);
				int numchildren = Integer.parseInt(cmd[2]);
				
				p.sendPacket(new ShowInterface(interfaceid));
				for(int i=0;i<=256;i++) {
				p.sendMessage("Trying conf "+i);
				for(int child=0;child<=numchildren;child++) {
					p.sendPacket(new SendInterfaceConfig(548, child, i));
				}
			}
			
			} catch(NumberFormatException nfe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::anim [id]");
			} catch(ArrayIndexOutOfBoundsException oobe) {
				p.sendMessage("<col=ab0000>Invalid format! Should be ::anim [id]");
			}
			
			break;
			
		case "home":
			p.getMovement().teleport(Engine.HOME);
			break;
		}
	}

}
