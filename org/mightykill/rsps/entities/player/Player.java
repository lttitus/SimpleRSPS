package org.mightykill.rsps.entities.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Combat;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.exchange.offers.BuyOffer;
import org.mightykill.rsps.exchange.offers.GEOffer;
import org.mightykill.rsps.interfaces.Interface;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.packets.incoming.IncomingPacket;
import org.mightykill.rsps.io.packets.outgoing.DisplayInterface;
import org.mightykill.rsps.io.packets.outgoing.Logout;
import org.mightykill.rsps.io.packets.outgoing.MapRegion;
import org.mightykill.rsps.io.packets.outgoing.OutgoingPacket;
import org.mightykill.rsps.io.packets.outgoing.PlaySound;
import org.mightykill.rsps.io.packets.outgoing.SendConfig;
import org.mightykill.rsps.io.packets.outgoing.SendConfig2;
import org.mightykill.rsps.io.packets.outgoing.SendItems;
import org.mightykill.rsps.io.packets.outgoing.SendMessage;
import org.mightykill.rsps.io.packets.outgoing.SendSkill;
import org.mightykill.rsps.io.packets.outgoing.ShowInterface;
import org.mightykill.rsps.io.packets.outgoing.UpdateGEOffer;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.world.zones.Zone;

public class Player extends Entity {
	
	private Client client;
	
	private boolean debug = false;
	private boolean connected = false;
	public boolean online = true;
	private int rights = 0;
	private String uuid = null;
	
	public int[] color = new int[5];
	public int gender = 0;
	public int[] pLook = new int[7];
	public int skullIcon = -1;
	private int idleAnim = 0x328;
	private int walkAnim = 0x333;
	private int runAnim = 0x338;
	
	private int seenPlayerCount = 0;
	private Player[] seenPlayers = new Player[255];	//The client can only 'see' 255 Entities of each type 
	private int seenNPCCount = 0;
	private NPC[] seenNPCs = new NPC[255];
	/*public ArrayList<Player> localPlayers = new ArrayList<Player>();
	public ArrayList<NPC> localNPCs = new ArrayList<NPC>();*/
	
	/* Update stuff */
	public String chatMessage;
	public int chatMessageEffects = 0;
	//private int originRegionX = -1, originRegionY = -1;
	public int pnpc = -1;
	
	/* Testing stuff */
	public int confTest = 0;
	public int testSound = 0;
	public int testSoundByte = 0;
	public int testgeconf = 0;
	
	public int idleCount = 0;
	public boolean isPlayingMusic = false;
	private int currentMusicId = -1;
	
	//private HUD displays;
	
	public Player(Client c, int _Id, String username, String uuid, int rights, String rawStats, Position pos) {
		super(pos);
		//movement.setDirection(1);	//North
		
		Point startRegion = getCurrentRegionPoint();
		movement.setActiveChunk(startRegion);
		/*originRegionX = startRegion.x;
		originRegionY = startRegion.y;*/
		
		pLook[0] = 0;
		pLook[1] = 10;
		pLook[2] = 18;
		pLook[3] = 26;
		pLook[4] = 33;
		pLook[5] = 36;
		pLook[6] = 42;
		
		//displays = new HUD();
		
		this.appearanceUpdated = true;
		
		for(int i=0;i<color.length;i++) {
			color[i] = _Id;
		}
		
		//System.out.println("Constructing new Player ["+username+"]: "+_Id);
		this._Id = _Id;
		this.name = username;
		this.client = c;
		connected = true;
		
		this.uuid = uuid;
		this.rights = rights;
		
		for(int skillId=0;skillId<24;skillId++) {
			String rawSkillData = rawStats.substring(skillId*9, skillId*9+9);
			int skillLevel = Integer.parseInt(rawSkillData.substring(0, 2), 16);
			int xp = Integer.parseInt(rawSkillData.substring(2, 9), 16);
			
			this.skillLevel[skillId] = skillLevel;
			this.skillXp[skillId] = xp;
		}
		//setLevel(Skill.HITPOINTS, 10);
	}
	
	/* TODO: Move the local Entities methods to another file */
	public Player[] getSeenPlayers() {
		return this.seenPlayers;
	}
	
	public NPC[] getSeenNPCs() {
		return this.seenNPCs;
	}
	
	public int getSeenPlayersCount() {
		return this.seenPlayerCount;
	}
	
	public int getSeenNPCsCount() {
		return this.seenNPCCount;
	}
	
	public int seePlayer(Player p) {
		int slot = getNextFreeSeenPlayerSlot();
		
		if(slot != -1) {
			sendMessage("Saw "+p.getName());
			seenPlayers[slot] = p;
			seenPlayerCount++;
		}
		
		return slot;
	}
	
	public int seeNPC(NPC n) {
		int slot = getNextFreeSeenNPCSlot();
		
		if(slot != -1) {
			seenNPCs[slot] = n;
			seenNPCCount++;
		}
		
		return slot;
	}
	
	public int getSeenPlayerIndex(Player p) {
		for(int slot=0;slot<seenPlayers.length;slot++) {
			if(seenPlayers[slot] == p) return slot;
		}
		
		return -1;
	}
	
	public int getSeenNPCIndex(NPC n) {
		for(int slot=0;slot<seenPlayers.length;slot++) {
			if(seenNPCs[slot] == n) return slot;
		}
		
		return -1;
	}
	
	public int forgetPlayer(Player p) {
		int slot = getSeenPlayerIndex(p);
		
		if(slot != -1) {
			sendMessage("Removing "+p.getName());
			seenPlayers[slot] = null;
			seenPlayerCount--;
		}
		
		return slot;
	}
	
	public int forgetNPC(NPC n) {
		int slot = getSeenNPCIndex(n);
		
		if(slot != -1) {
			seenNPCs[slot] = null;
			seenNPCCount--;
		}
		
		return slot;
	}
	
	private int getNextFreeSeenPlayerSlot() {
		if(seenPlayerCount == 255) return -1;	//Don't waste any CPU cycles if we already know we are full
		for(int slot=0;slot<seenPlayers.length;slot++) {
			if(seenPlayers[slot] == null) return slot;
		}
		
		return -1;
	}
	
	private int getNextFreeSeenNPCSlot() {
		if(seenNPCCount == 255) return -1;
		for(int slot=0;slot<seenNPCs.length;slot++) {
			if(seenNPCs[slot] == null) return slot;
		}
		
		return -1;
	}
	
	public void toggleDebug() {
		this.debug = !debug;
	}
	
	public boolean isDebug() {
		return this.debug;
	}
	
	public void refreshSkills() {
		for(int skillId=0;skillId<24;skillId++) {
			setLevel(skillId, skillLevel[skillId]);
			//this.sendPacket(new SendSkill(this, skillId));
		}
	}
	
	public void loggedIn() {
		online = true;
		giveItem(4151, 1);
		client.setLoggedIn(true);
		sendMessage("Welcome to SimpleRSPS!");
		refreshSkills();
		//setLevelByXP(Skill.HITPOINTS, 1171);
	}
	
	private Interface currentInterface = null;
	public void showInterface(Interface i) {
		this.currentInterface = i;
		if(i != null) {
			sendPacket(new ShowInterface(i.getInterfaceId(), i.getLocation(), i.getWindowId(), i.getShowId()));
		}
	}
	
	private Interface chatboxInterface = null;
	public void showChatboxInterface(Interface i) {
		this.chatboxInterface = i;
		if(i != null) {
			sendPacket(new ShowInterface(i.getInterfaceId(), i.getLocation(), i.getWindowId(), i.getShowId()));
		}
	}
	
	private Interface currentOverlay = null;
	public void showOverlay(Interface i) {
		this.currentOverlay = i;
		if(i != null) {
			sendPacket(new ShowInterface(i.getInterfaceId(), i.getLocation(), i.getWindowId(), i.getShowId()));
		}
	}
	
	public Interface getOverlay() {
		return this.currentOverlay;
	}
	
	public Interface getChatboxInterface() {
		return this.chatboxInterface;
	}
	
	public void closeChatboxInterface() {
		if(this.chatboxInterface != null) {
			this.chatboxInterface.close(this);
			sendPacket(new ShowInterface(137, chatboxInterface.getLocation(), 752, chatboxInterface.getShowId()));	//Reset the interface
			this.chatboxInterface = null;
		}
	}
	
	public Interface getShownInterface() {
		return this.currentInterface;
	}
	
	public void closeInterface() {
		if(this.currentInterface != null) {
			this.currentInterface.close(this);
			this.currentInterface = null;
		}
	}
	
	public void closeOverlay() {
		if(this.currentOverlay != null) {
			this.currentOverlay.close(this);
			sendPacket(new ShowInterface(208, 8, 548, 1));	//Reset the interface
			this.currentOverlay = null;
		}
	}
	
	public void sendConfig(int confid, int set) {
		if(set > 255) {
			sendPacket(new SendConfig2(confid, set));
		}else {
			sendPacket(new SendConfig(confid, set));
		}
	}
	
	/**
	 * Blinks an icon on the Player's sidebar
	 * @param iconId
	 */
	public void blinkSidebarIcon(int hudId) {
		sendPacket(new SendConfig(1021, hudId+1));
	}
	
	/**
	 * Blinks icon(s) in the skill tree
	 * @param iconIds
	 */
	public int testBlink = 0;
	public void blinkSkillIcon(int mask) {
		sendPacket(new SendConfig(1179, mask));
	}
	
	public void updateQuestPoints(int qp) {
		sendPacket(new SendConfig(101, qp));
	}
	
	public void sendPacket(OutgoingPacket packet) {
		client.sendPacket(packet);
	}
	
	public void setClient(Client c) {
		this.client = c;
	}
	
	public Client getClient() {
		return this.client;
	}
	
	public int getRights() {
		return this.rights;
	}
	
	public void refreshInventory() {
		sendPacket(
				new SendItems(149,
						0,
						93, 
						inventory));
	}
	
	public void refreshEquipment() {
		sendPacket(
				new SendItems(387, 
						28,
						93, 
						equipment));
	}
	
	public GEOffer getOffer(int slot) {
		return this.geOffers[slot];
	}
	
	/**
     * Setting a tab.
     * @param p The Player which the frame should be created for.
     * @param tabId Which tab to display the interface on.
     * @param childId The interface to display on the tab.
     */
    public void setTabInterface(int tabId, int childId) {
    	sendPacket(
    			new DisplayInterface(tabId, 
    					childId, 
    					1, 
    					childId == 137 ? 752 : 548)
    			);
    }
    
    /*public void setHUD(int hudId, Interface i) {
    	this.displays.setDisplay(hudId, i);
    	setTabInterface(73+hudId, i.getInterfaceId());
    }*/
    
    public void initInterfaces() {
    	setTabInterface(6, 745);
    	setTabInterface(11, 751); // Chat options
    	setTabInterface(68, 752); // Chatbox
    	setTabInterface(64, 748); // HP bar
    	setTabInterface(65, 749); // Prayer bar
    	setTabInterface(66, 750); // Energy bar
    	setTabInterface(67, 747);
        sendConfig(1160, -1);
        setTabInterface(8, 137); // Playername on chat
        //setHUD(0, new AttackInterface());
        //setHUD(1, new SkillsInterface());
        setTabInterface(73,  92); // Attack tab
        setTabInterface(74, 320); // Skill tab
        setTabInterface(75, 274); //  Quest tab
        setTabInterface(76, 149); // Inventory tab
        setTabInterface(77, 387); // Equipment tab
        setTabInterface(78, 271); // Prayer tab
        setTabInterface(79, 192/*Magic.spellBooks[p.spellBook]*/); // Magic tab - 192 = Regular
        setTabInterface(81, 550); // Friend tab
        setTabInterface(82, 551); // Ignore tab
        setTabInterface(83, 589); // Clan tab
        setTabInterface(84, 261); // Setting tab
        setTabInterface(85, 464); // Emote tab
        setTabInterface(86, 187); // Music tab
        setTabInterface(87, 182); // Logout tab
    }
    
    protected void clearFlags() {
    	this.chatMessage = null;
    	this.chatMessageEffects = 0;
    }
    
    public void setIdleAnimation(int anim) {
    	this.idleAnim = anim;
    }
    
    public int getIdleAnimation() {
    	return this.idleAnim;
    }
    
    public void setWalkAnimation(int anim) {
    	this.walkAnim = anim;
    }
    
    public int getWalkAnimation() {
    	return this.walkAnim;
    }
	
	public boolean canAttack(Entity attacker) {
		if(attacker instanceof NPC) return true;
		return isInPvpZone();
	}
	
	public boolean isInPvpZone() {
		for(Zone z:Engine.zones.getApplicableZones(this)) {
			if(z != null) {
				if(z.isPvp()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void setLevel(int skillId, int level) {
		super.setLevel(skillId, level);
		this.sendPacket(new SendSkill(this, skillId));
		this.combatLevel = Combat.calculateCombat(this);
		this.appearanceUpdated = true;
	}

	public int getServerId() {
		return this._Id;
	}

	private boolean wasInMulti = false;
	private boolean wasInPvp = false;
	protected void process(long curTick) {
		/*boolean inMulti = isInMulticombat();
		if(inMulti && !wasInMulti) {	//Currently in multi
			sendPacket(new SendInterfaceConfig(745, 1, 0));
		}else if(!inMulti && wasInMulti) {	//No longer in multi
			sendPacket(new SendInterfaceConfig(745, 1, 1));
		}
		wasInMulti = inMulti;
		
		boolean inPvp = isInPvpZone();
		if(inPvp && !wasInPvp) {	//Currently in pvp
			sendPacket(new ShowInterface(381, true));
		}else if(!inPvp && wasInPvp) {	//No longer in pvp
			sendPacket(new ShowInterface(208, 8, 548, 1));
		}
		wasInPvp = inPvp;*/
		
		/*for(Iterator<IncomingPacket> it=this.client.getIncomingPackets().iterator();it.hasNext();) {
			IncomingPacket packet = it.next();
			if(packet != null) {
				packet.handlePacket();
			}
			
			it.remove();
		}*/
		
		/*if(idleCount > 0) {
			if(!(currentOverlay instanceof IdleInterface)) {
				showOverlay(new IdleInterface(this, curTick));
			}else {
				((IdleInterface)currentOverlay).update(this, curTick);
			}
			if(idleCount >= 20) {	//~5 minutes
				System.out.println("Disconnecting "+name+" for being idle too long.");
				this.disconnect();
			}
		}else {
			if(currentOverlay instanceof IdleInterface) {
				closeOverlay();
			}
		}*/
		
		/* Load a new Region (13 x 13 Chunks) when they reach a black border.
		 * This is used in many calculations, such as placement of items and NPCs */
		Point currRegionPoint = getCurrentRegionPoint();
		Point activeChunk = movement.getActiveChunk();
		
		if(currRegionPoint.x >= activeChunk.x + 5 || currRegionPoint.x <= activeChunk.x - 5 || 
			currRegionPoint.y >= activeChunk.y + 5 || currRegionPoint.y <= activeChunk.y - 5) {
			sendPacket(new MapRegion(this));
			movement.setActiveChunk(currRegionPoint);
		}
	}
	
	/*public int getMusicPlaying() {
		return this.currentMusicId;
	}
	
	public void playMusic(int musicId) {
		this.isPlayingMusic = true;
		this.currentMusicId = musicId;
		sendPacket(new PlayMusic(musicId));
	}*/
	
	/**
	 * Sends a message to the Player
	 * @param msg
	 */
	public void sendMessage(String msg) {
		sendPacket(new SendMessage(msg));
	}
	
	public void debug(String msg) {
		if(debug) sendMessage(msg);
	}
	
	public void login() {
		this.client.setLoggedIn(true);
	}
	
	public void disconnect() {
		this.client.sendPacket(new Logout());
		this.connected = false;
		this.client.setLoggedIn(false);
		this.movement.getCurrentRegion().removeEntity(this);
		if(combat.getAttacking() != null) combat.getAttacking().getCombat().removeAttacker(this);
		if(!Engine.players.savePlayer(this)) {
			System.err.println("Unable to save Player!");
		}
	}
	
	public boolean isConnected() {
		return this.connected;
	}

	public void updateExchangeOffer(GEOffer offer, boolean notify) {
		int totalOffered = offer.getQuantityOffered();
		int totalRequisitioned = totalOffered-offer.getRemaining();	//# bought or sold
		
		if(notify) {
			boolean bought = offer instanceof BuyOffer;
			if(offer.getRemaining() > 0) {
				sendMessage("<col=ab00ab>You have "+(bought?"bought":"sold")+" "+totalRequisitioned+"/"+totalOffered+" "+Engine.items.getDefinition(offer.getItemId()).getName());	//TODO: Name the item
			}else {
				sendMessage("<col=00ab00>Your "+(bought?"buy":"sale")+" offer of "+totalOffered+"x "+Engine.items.getDefinition(offer.getItemId()).getName()+" is complete!");	//TODO: Name the item
				playSound(4042);
			}
			//TODO: Play GE offer sound
		}
		
		this.sendPacket(new UpdateGEOffer(offer.getSlot(), offer.getStatus(), offer.getItemId(), offer.getAmount(), totalOffered, totalRequisitioned, offer.getTotalAmount()));
		
		this.sendPacket(new SendItems(-1, -1757-offer.getSlot(), 523+offer.getSlot(), offer.getCoffers()));
	}
	
	public void playSound(int soundId) {
		sendPacket(new PlaySound(soundId, 1));
	}
	
	public String getUUID() {
		return this.uuid;
	}
	
	

	public void setRunAnimation(int anim) {
		this.runAnim = anim;
	}

	public void teleport(int x, int y, int h) {
		movement.teleport(new Position(x, y, 0));
		sendPacket(new MapRegion(this));
	}
	
	public void teleport(int x, int y) {
		teleport(x, y, 0);
	}

	public Position getRespawnPoint() {
		return Engine.HOME;
	}

	protected long getRespawnTime() {
		return 8;
	}

}
