package org.mightykill.rsps.io.client;

import java.awt.Dimension;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.PacketUtils;
import org.mightykill.rsps.io.packets.incoming.AcceptTradeRequest;
import org.mightykill.rsps.io.packets.incoming.ActionButton;
import org.mightykill.rsps.io.packets.incoming.AttackNPC;
import org.mightykill.rsps.io.packets.incoming.AttackPlayer;
import org.mightykill.rsps.io.packets.incoming.CameraPosition;
import org.mightykill.rsps.io.packets.incoming.ChatMessage;
import org.mightykill.rsps.io.packets.incoming.ClosedInterface;
import org.mightykill.rsps.io.packets.incoming.Command;
import org.mightykill.rsps.io.packets.incoming.CreateAccount;
import org.mightykill.rsps.io.packets.incoming.DropItem;
import org.mightykill.rsps.io.packets.incoming.EquipItem;
import org.mightykill.rsps.io.packets.incoming.ExamineItem;
import org.mightykill.rsps.io.packets.incoming.ExamineNPC;
import org.mightykill.rsps.io.packets.incoming.ExamineObject;
import org.mightykill.rsps.io.packets.incoming.FollowPlayer;
import org.mightykill.rsps.io.packets.incoming.FreshLogin;
import org.mightykill.rsps.io.packets.incoming.GEItemSelect;
import org.mightykill.rsps.io.packets.incoming.IdlePing;
import org.mightykill.rsps.io.packets.incoming.IncomingPacket;
import org.mightykill.rsps.io.packets.incoming.InitiateLogin;
import org.mightykill.rsps.io.packets.incoming.ItemOnItem;
import org.mightykill.rsps.io.packets.incoming.ItemSelect;
import org.mightykill.rsps.io.packets.incoming.MouseEvent;
import org.mightykill.rsps.io.packets.incoming.MusicPacket;
import org.mightykill.rsps.io.packets.incoming.NPCSpeak;
import org.mightykill.rsps.io.packets.incoming.ObjectInteraction;
import org.mightykill.rsps.io.packets.incoming.PingPacket;
import org.mightykill.rsps.io.packets.incoming.QuickChat;
import org.mightykill.rsps.io.packets.incoming.ReconnectSession;
import org.mightykill.rsps.io.packets.incoming.RegionUpdate;
import org.mightykill.rsps.io.packets.incoming.StringInput;
import org.mightykill.rsps.io.packets.incoming.SwitchInterfaceItems;
import org.mightykill.rsps.io.packets.incoming.SwitchItems;
import org.mightykill.rsps.io.packets.incoming.TakeItem;
import org.mightykill.rsps.io.packets.incoming.TradePlayer;
import org.mightykill.rsps.io.packets.incoming.UnequipItem;
import org.mightykill.rsps.io.packets.incoming.UpdateServer;
import org.mightykill.rsps.io.packets.incoming.Walking;
import org.mightykill.rsps.io.packets.incoming.WindowEvent;
import org.mightykill.rsps.io.packets.incoming.WorldList;
import org.mightykill.rsps.io.packets.outgoing.OutgoingPacket;

public class Client {
	
	private ClientSocket socket;
	private ArrayList<IncomingPacket> inPacketQueue = new ArrayList<IncomingPacket>();
	private ArrayList<OutgoingPacket> outPacketQueue = new ArrayList<OutgoingPacket>();
	
	private Dimension clientDim;
	private boolean screenActive = true;
	private boolean loggedIn = false;
	
	public Client(Socket s) throws IOException {
		this.socket = new ClientSocket(s);
	}
	
	public boolean sendPacket(OutgoingPacket p) {
		return outPacketQueue.add(p);
	}
	
	public ClientSocket getSocket() {
		return this.socket;
	}
	
	public IncomingPacket getNextPacket(Player p, boolean packetSizeIsWord) {
		IncomingPacket packet = null;
		
		try {
			if(this.socket.getAvail() < 1) return null;	//Stop looking if there is no more data
				
				int packetId = socket.readByte() & 0xFF;
				
				if(packetId != 0) {
					int packetSize = PacketUtils.PACKET_SIZES[packetId];
					if(packetSize == -1) {	//-1 means variable packet size, sent from the Client
						if(packetSizeIsWord) {
							packetSize = ((socket.readByte() & 0xff) << 8) + socket.readByte() & 0xff;
						}else {
							packetSize = socket.readByte() & 0xff;
						}
					}
					if(packetSize > 500) this.close();	//No Incoming Packets are over x bytes; Probably a crasher - Close any further communication
					//System.out.println("Packet Id: "+packetId+"; Size: "+packetSize);
					
					byte[] data = socket.readBytes(packetSize);
					switch(packetId) {
					case 3:	//Equip Item
						packet = new EquipItem(data, p);
						break;
					case 7:	//NPC Speak
						packet = new NPCSpeak(data, p);
						break;
					case 14:	//Initiate Login
						packet = new InitiateLogin(data, this);
						break;
					case 15:	//Update Server
						packet = new UpdateServer(data, this);
						break;
					case 16:	//Fresh Login
					case 18:
						packet = new FreshLogin(packetId, packetSize, data, this);
						break;
					case 17:	//JAGGrab
						System.out.println("JAGGrab");
						break;
					case 22:	//Region Update
						packet = new RegionUpdate(data, p);
						break;
					case 37:
						packet = new FollowPlayer(data, p);
						break;
					case 38:	//Inventory examine
					case 90:	//Bank examine
						packet = new ExamineItem(packetId, data, p);
						break;
					case 40:
						packet = new ItemOnItem(data, p);
						break;
					case 42:	//String input
						packet = new StringInput(data, p);
						break;
					case 46:	//Object Interaction
					case 94:
					case 158:
					case 190:
					case 228:
						packet = new ObjectInteraction(packetId, packetSize, data, p);
						break;
					case 47:	//Idle
						packet = new IdlePing(p);
						break;
					case 49:	//Main Walking
					case 119:	//Minimap Walking
					case 138:	//Other Walking
						packet = new Walking(packetId, packetSize, data, p);
						break;
					case 59:	//Mouse Clicked on Screen
						packet = new MouseEvent(data, this);
						break;
					case 84:	//Object Examine
						packet = new ExamineObject(data, p);
						break;
					case 85:	//Create Account
						packet = new CreateAccount(data, this);
						break;
					case 88:	//Examine NPC
						packet = new ExamineNPC(data, p);
						break;
					case 99:	//Camera Position changed
						packet = new CameraPosition(data, p);
						break;
					case 107:	//Command ::
						packet = new Command(packetSize, data, p);
						break;
					case 108:	//Closed Interface
						packet = new ClosedInterface(data, p);
						break;
					case 115:	//Ping
					case 117:
						packet = new PingPacket(packetId, data, this);
						break;
					case 21:
					case 113:	//ActionButton
					case 169:
					case 214:
					case 232:
					case 233:
						packet = new ActionButton(packetId, packetSize, data, p);
						break;
					case 123:	//Attack NPC
						packet = new AttackNPC(data, p);
						break;
					case 160:	//Attack Player
						packet = new AttackPlayer(data, p);
						break;
					case 167:	//Switch Items
						packet = new SwitchItems(data, p);
						break;
					case 179:	//Switch Items on an Interface, such as the bank
						packet = new SwitchInterfaceItems(data, p);
						break;
					case 182:	//Reconnecting Session
					case 183:
						packet = new ReconnectSession(packetId, packetSize, data, this);
						break;
					case 195:	//GE Item Search Selection
						packet = new GEItemSelect(data, p);
						break;
					case 201:	//Take item from the ground
						packet = new TakeItem(data, p);
						break;
					case 203:
						packet = new UnequipItem(data, p);
						break;
					case 211:	//Drop Item
						packet = new DropItem(data, p);
						break;
					case 220:
						packet = new ItemSelect(data, p);
						break;
					case 222:	//Chat Message
						packet = new ChatMessage(data, p);
						break;
					case 227:	//Player trade
						packet = new TradePlayer(data, p);
						break;
					case 247:	//Music
						packet = new MusicPacket(data, p);
						break;
					case 248:	//Window Event
						packet = new WindowEvent(data, this);
						break;
					case 250:
						packet = new QuickChat(data, this);
						break;
					case 253:	//Accept trade request from chat box
						packet = new AcceptTradeRequest(data, p);
						break;
					case 255:
						packet = new WorldList(data, this);
						break;
						
					default:
						System.err.println("Unhandled PacketId: "+packetId+"; Packet Size: "+packetSize);
						break;
					}
				}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return packet;
	}
	
	public IncomingPacket getNextPacket(Player p) {
		return getNextPacket(p, false);
	}
	
	/**
	 * Receives the next 8 packets OR however many are ready
	 */
	public void receivePackets(Player p) {
		for(int i=0;i<8;i++) {
			IncomingPacket packet = getNextPacket(p);
			inPacketQueue.add(packet);
		}
	}
	
	public void close() {
		this.loggedIn = false;
		this.socket.close();
	}
	
	public ArrayList<IncomingPacket> getIncomingPackets() {
		return this.inPacketQueue;
	}
	
	public ArrayList<OutgoingPacket> getOutgoingPackets() {
		return this.outPacketQueue;
	}
	
	public void setDimensions(int w, int h) {
		System.out.println("Setting client dimensions to "+w+", "+h);
		this.clientDim = new Dimension(w, h);
	}
	
	public Dimension getDimensions() {
		return this.clientDim;
	}
	
	public void setScreenFocus(boolean active) {
		this.screenActive = active;
		//System.out.println("Client screen is "+(active?"in":"out of")+" focus.");
	}
	
	public void setLoggedIn(boolean log) {	//TODO: Make this not exploitable
		this.loggedIn = log;
	}
	
	public boolean isLoggedIn() {
		return this.loggedIn;
	}

}
