package org.mightykill.rsps.entities.player;

import java.awt.Point;
import java.util.ArrayList;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Combat;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.trade.TradeOffer;
import org.mightykill.rsps.exchange.offers.BuyOffer;
import org.mightykill.rsps.exchange.offers.GEOffer;
import org.mightykill.rsps.interfaces.BankInterface;
import org.mightykill.rsps.interfaces.Interface;
import org.mightykill.rsps.interfaces.TradeInterface;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.packets.outgoing.DisplayInterface;
import org.mightykill.rsps.io.packets.outgoing.Logout;
import org.mightykill.rsps.io.packets.outgoing.MapRegion;
import org.mightykill.rsps.io.packets.outgoing.OutgoingPacket;
import org.mightykill.rsps.io.packets.outgoing.PlaySound;
import org.mightykill.rsps.io.packets.outgoing.SendAccessMask;
import org.mightykill.rsps.io.packets.outgoing.SendConfig;
import org.mightykill.rsps.io.packets.outgoing.SendConfig2;
import org.mightykill.rsps.io.packets.outgoing.SendInterfaceConfig;
import org.mightykill.rsps.io.packets.outgoing.SendItems;
import org.mightykill.rsps.io.packets.outgoing.SendMessage;
import org.mightykill.rsps.io.packets.outgoing.SendRunEnergy;
import org.mightykill.rsps.io.packets.outgoing.SendSkill;
import org.mightykill.rsps.io.packets.outgoing.ShowInterface;
import org.mightykill.rsps.io.packets.outgoing.UpdateGEOffer;
import org.mightykill.rsps.items.containers.Bank;
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
	public int[] pLook = {0, 10, 18, 26, 33, 36, 42};	//Defaults
	public int skullIcon = -1;
	private int idleAnim = 0x328;
	private int walkAnim = 0x333;
	private int runAnim = 0x338;

	private ArrayList<Player> seenPlayers = new ArrayList<Player>();
	private ArrayList<NPC> seenNPCs = new ArrayList<NPC>();

	/* Update stuff */
	public String chatMessage;
	public int chatMessageEffects = 0;
	public int pnpc = -1;

	/* Testing stuff */
	public int confTest = 0;
	public int testSound = 0;
	public int testSoundByte = 0;
	public int testgeconf = 0;

	public int idleCount = 0;
	public boolean isPlayingMusic = false;
	private int currentMusicId = -1;

	public Player(Client c, int _Id, String username, String uuid, int rights, String rawStats, Position pos,
			String rawAppearance) {
		super(pos);

		Point startRegion = getCurrentRegionPoint();
		movement.setActiveChunk(startRegion);

		// Look defaults
		/*pLook[0] = 0;
		pLook[1] = 10;
		pLook[2] = 18;
		pLook[3] = 26;
		pLook[4] = 33;
		pLook[5] = 36;
		pLook[6] = 42;*/

		for (int lookIndex = 0; lookIndex < 6; lookIndex++) { // TODO: Make this a bit nicer
			String rawHex = rawAppearance.substring(lookIndex * 2, lookIndex * 2 + 2);
			pLook[lookIndex] += Integer.parseInt(rawHex.substring(0, 1), 16);
			if (lookIndex < 5) {
				color[lookIndex] = Integer.parseInt(rawHex.substring(1), 16);
			}
		}

		this._Id = _Id;
		this.name = username;
		this.client = c;
		connected = true;

		this.uuid = uuid;
		this.rights = rights;

		for (int skillId = 0; skillId < 24; skillId++) {
			String rawSkillData = rawStats.substring(skillId * 9, skillId * 9 + 9);
			int skillLevel = Integer.parseInt(rawSkillData.substring(0, 2), 16);
			int xp = Integer.parseInt(rawSkillData.substring(2, 9), 16);

			this.skillLevel[skillId] = skillLevel;
			this.skillXp[skillId] = xp;
		}
	}

	public ArrayList<Player> getSeenPlayers() {
		return (ArrayList<Player>) this.seenPlayers.clone();
	}

	public boolean seePlayer(Player p) {
		if (this.seenPlayers.size() == 255)
			return false;
		return this.seenPlayers.add(p);
	}
	
	public Player getSeenPlayer(int localId) {
		return seenPlayers.get(localId-1);
	}

	public boolean forgetPlayer(Player p) {
		return this.seenPlayers.remove(p);
	}

	public ArrayList<NPC> getSeenNPCs() {
		return (ArrayList<NPC>) this.seenNPCs.clone();
	}

	public boolean seeNPC(NPC p) {
		if (this.seenNPCs.size() == 255)
			return false;
		return this.seenNPCs.add(p);
	}

	public boolean forgetNPC(NPC p) {
		return this.seenNPCs.remove(p);
	}

	public void toggleDebug() {
		this.debug = !debug;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public void refreshSkills() {
		for (int skillId = 0; skillId < 24; skillId++) {
			setLevel(skillId, skillLevel[skillId]);
		}
	}

	public void loggedIn() {
		online = true;
		sendConfig(173, movement.isRunning() ? 1 : 0);
		sendPacket(new SendRunEnergy(movement.getEnergy()));
		giveItem(4151, 1);
		client.setLoggedIn(true);
		this.appearanceUpdated = true;
		sendMessage("Welcome to SimpleRSPS!");
		refreshSkills();
	}
	
	private boolean tryingifc = false;
	private int ifciface, ifcmaxchildren, ifccurrchild, ifcset;
	public void tryIFC(int ifaceId, int maxChildren, int startChild, int set) {
		if(debug) {
			this.ifciface = ifaceId;
			this.ifcmaxchildren = maxChildren;
			this.ifccurrchild = startChild;
			this.ifcset = set;
			
			tryingifc = true;
		}
	}
	
	private boolean testingInterfaces = false;
	private int currentTestInterface = 0;
	private int maxIfTestTicks = 1;
	private int currentIfTestTicks = 0;
	public void testInterfaces(int start, int maxTicks) {
		if(debug) {
			this.maxIfTestTicks = maxTicks;
			testingInterfaces = true;
			this.currentTestInterface = start;
			debug("Now testing interfaces, "+maxIfTestTicks+" ticks per interface");
		}
	}
	
	private boolean testingConfig = false;
	private int currentConfig = 0;
	public void testConfig(int start) {
		currentConfig = start;
		if(!testingConfig && debug) {
			testingConfig = true;
			debug("Now testing configuration settings");
		}
	}

	private Interface currentInterface = null;
	public void showInterface(Interface i) {
		this.currentInterface = i;
		if (i != null) {
			sendPacket(new ShowInterface(i.getInterfaceId(), i.getLocation(), i.getWindowId(), i.getShowId()));
		}
		currentInterface.show(this);
	}
	
	public void updateInterface() {
		if(currentInterface != null) {
			currentInterface.update(this);
		}
	}

	private Interface chatboxInterface = null;
	public void showChatboxInterface(Interface i) {
		this.chatboxInterface = i;
		if (i != null) {
			sendPacket(new ShowInterface(i.getInterfaceId(), i.getLocation(), i.getWindowId(), i.getShowId()));
		}
	}

	private Interface currentOverlay = null;
	public void showOverlay(Interface i) {
		this.currentOverlay = i;
		if (i != null) {
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
		if (this.chatboxInterface != null) {
			this.chatboxInterface.close(this);
			sendPacket(new ShowInterface(137, chatboxInterface.getLocation(), 752, chatboxInterface.getShowId())); // Reset
																													// the
																													// interface
			this.chatboxInterface = null;
		}
	}

	public Interface getShownInterface() {
		return this.currentInterface;
	}

	public void closeInterface() {
		if (this.currentInterface != null) {
			Interface tmp = this.currentInterface;
			
			this.currentInterface = null;
			tmp.close(this);
		}
		
		destroyInterface();
	}

	public void removeOverlay() {
		if (this.currentOverlay != null) {
			Interface tmp = this.currentOverlay;
			
			this.currentOverlay = null;
			tmp.close(this);
		}
		
		destroyInterface();
	}
	
	private void destroyInterface() {
		sendPacket(new ShowInterface(208, 8, 548, 1)); // Reset the interface
	}

	public void sendConfig(int confid, int set) {
		if (set > 255) {
			sendPacket(new SendConfig2(confid, set));
		} else {
			sendPacket(new SendConfig(confid, set));
		}
	}

	/**
	 * Blinks an icon on the Player's sidebar
	 * 
	 * @param iconId
	 */
	public void blinkSidebarIcon(int hudId) {
		sendPacket(new SendConfig(1021, hudId + 1));
	}

	/**
	 * Blinks icon(s) in the skill tree
	 * 
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
		sendPacket(new SendItems(149, 0, 93, inventory));
	}

	public void refreshEquipment() {
		sendPacket(new SendItems(387, 28, 93, equipment));
	}

	public GEOffer getOffer(int slot) {
		return this.geOffers[slot];
	}

	/**
	 * Setting a tab.
	 * 
	 * @param p
	 *            The Player which the frame should be created for.
	 * @param tabId
	 *            Which tab to display the interface on.
	 * @param childId
	 *            The interface to display on the tab.
	 */
	public void setTabInterface(int tabId, int childId) {
		sendPacket(new DisplayInterface(tabId, childId, 1, childId == 137 ? 752 : 548));
	}
	
	public boolean isBanking() {
		return currentInterface instanceof BankInterface;
	}
	
	public boolean isTrading() {
		return currentInterface instanceof TradeInterface;
	}

	public void openBank() {
		showInterface(new BankInterface());
	}

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
		// setHUD(0, new AttackInterface());
		// setHUD(1, new SkillsInterface());
		setTabInterface(73, 92); // Attack tab
		setTabInterface(74, 320); // Skill tab
		setTabInterface(75, 274); // Quest tab
		setTabInterface(76, 149); // Inventory tab
		setTabInterface(77, 387); // Equipment tab
		setTabInterface(78, 271); // Prayer tab
		setTabInterface(79, 192/* Magic.spellBooks[p.spellBook] */); // Magic tab - 192 = Regular
		setTabInterface(81, 550); // Friend tab
		setTabInterface(82, 551); // Ignore tab
		setTabInterface(83, 589); // Clan tab
		setTabInterface(84, 261); // Setting tab
		setTabInterface(85, 464); // Emote tab
		setTabInterface(86, 187); // Music tab
		setTabInterface(87, 182); // Logout tab
	}
	
	public void addTradeOffer(TradeOffer offer) {
		this.tradeOffers.add(offer);
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
		if (attacker instanceof NPC)
			return true;
		return isInPvpZone();
	}

	public boolean isInPvpZone() {
		for (Zone z : Engine.zones.getApplicableZones(this)) {
			if (z != null) {
				if (z.isPvp()) {
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
		/*
		 * boolean inMulti = isInMulticombat(); if(inMulti && !wasInMulti) { //Currently
		 * in multi sendPacket(new SendInterfaceConfig(745, 1, 0)); }else if(!inMulti &&
		 * wasInMulti) { //No longer in multi sendPacket(new SendInterfaceConfig(745, 1,
		 * 1)); } wasInMulti = inMulti;
		 * 
		 * boolean inPvp = isInPvpZone(); if(inPvp && !wasInPvp) { //Currently in pvp
		 * sendPacket(new ShowInterface(381, true)); }else if(!inPvp && wasInPvp) { //No
		 * longer in pvp sendPacket(new ShowInterface(208, 8, 548, 1)); } wasInPvp =
		 * inPvp;
		 */

		/*
		 * if(idleCount > 0) { if(!(currentOverlay instanceof IdleInterface)) {
		 * showOverlay(new IdleInterface(this, curTick)); }else {
		 * ((IdleInterface)currentOverlay).update(this, curTick); } if(idleCount >= 20)
		 * { //~5 minutes
		 * System.out.println("Disconnecting "+name+" for being idle too long.");
		 * this.disconnect(); } }else { if(currentOverlay instanceof IdleInterface) {
		 * closeOverlay(); } }
		 */

		/*
		 * Load a new Region (13 x 13 Chunks) when they reach a black border. This is
		 * used in many calculations, such as placement of items and NPCs
		 */
		Point currRegionPoint = getCurrentRegionPoint();
		Point activeChunk = movement.getActiveChunk();

		if (currRegionPoint.x >= activeChunk.x + 5 || currRegionPoint.x <= activeChunk.x - 5
				|| currRegionPoint.y >= activeChunk.y + 5 || currRegionPoint.y <= activeChunk.y - 5) {
			sendPacket(new MapRegion(this));
			movement.setActiveChunk(currRegionPoint);
		}
		
		if(tryingifc) {
			sendMessage("Testing interface: "+ifciface+", child: "+ifccurrchild+", set: "+ifcset);
			sendPacket(new SendInterfaceConfig(ifciface, ifccurrchild, ifcset));
			if(ifccurrchild != ifcmaxchildren) {
				ifccurrchild++;
			}else {
				tryingifc = false;
				sendMessage("Completed testing");
			}
		}
		
		if(testingConfig) {
			debug("Testing config: "+currentConfig);
			sendConfig(currentConfig, 1);
			if(currentConfig < 2000) {
				currentConfig++;
			}else {
				testingConfig = false;
				debug("Testing complete");
			}
		}
		
		if(testingInterfaces) {
			
			if(currentTestInterface < 781) {
				if(currentIfTestTicks < maxIfTestTicks) {
					currentIfTestTicks++;
				}else {
					debug("Interface: "+currentTestInterface);
					sendPacket(new ShowInterface(currentTestInterface));
					currentTestInterface++;
					currentIfTestTicks = 0;
				}
			}else {
				testingInterfaces = false;
				debug("Testing complete");
			}
		}
	}

	/*
	 * public int getMusicPlaying() { return this.currentMusicId; }
	 * 
	 * public void playMusic(int musicId) { this.isPlayingMusic = true;
	 * this.currentMusicId = musicId; sendPacket(new PlayMusic(musicId)); }
	 */

	/**
	 * Sends a message to the Player
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		sendPacket(new SendMessage(msg));
	}

	public void debug(String msg) {
		if (debug)
			sendMessage(msg);
	}

	public void login() {
		this.client.setLoggedIn(true);
	}

	public void disconnect() {
		this.client.sendPacket(new Logout());
		this.closeInterface();
		this.closeChatboxInterface();
		this.removeOverlay();
		this.connected = false;
		this.client.setLoggedIn(false);
		// this.movement.getCurrentRegion().removeEntity(this);
		if (combat.getAttacking() != null)
			combat.getAttacking().getCombat().removeAttacker(this);
		if (!Engine.players.savePlayer(this)) {
			System.err.println("Unable to save Player!");
		}
		Engine.players.removePlayer(this);
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void updateExchangeOffer(GEOffer offer, boolean notify) {
		int totalOffered = offer.getQuantityOffered();
		int totalRequisitioned = totalOffered - offer.getRemaining(); // # bought or sold

		if (notify) {
			boolean bought = offer instanceof BuyOffer;
			if (offer.getRemaining() > 0) {
				sendMessage("<col=ab00ab>You have " + (bought ? "bought" : "sold") + " " + totalRequisitioned + "/"
						+ totalOffered + " " + Engine.items.getDefinition(offer.getItemId()).getName()); // TODO: Name
																											// the item
			} else {
				sendMessage("<col=00ab00>Your " + (bought ? "buy" : "sale") + " offer of " + totalOffered + "x "
						+ Engine.items.getDefinition(offer.getItemId()).getName() + " is complete!"); // TODO: Name the
																										// item
				playSound(4042);
			}
			// TODO: Play GE offer sound
		}

		this.sendPacket(new UpdateGEOffer(offer.getSlot(), offer.getStatus(), offer.getItemId(), offer.getAmount(),
				totalOffered, totalRequisitioned, offer.getTotalAmount()));

		this.sendPacket(new SendItems(-1, -1757 - offer.getSlot(), 523 + offer.getSlot(), offer.getCoffers()));
	}
	
	public TradeOffer getTradeOffer(Player tradee) {
		for(TradeOffer offer:tradeOffers) {
			if(offer.getTradee() == tradee) {
				return offer;
			}
		}
		
		return null;
	}
	
	public void initiateTrade(Entity tradee) {
		faceEntity(tradee);
		showInterface(new TradeInterface((Player)tradee));
		sendMessage("You are now trading with "+tradee.getName());
		TradeOffer offer = getTradeOffer((Player)tradee);
		if(offer != null) {
			offer.expire();
		}
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

	public Bank getBank() {
		return this.bank;
	}

}
