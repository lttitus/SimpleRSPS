package org.mightykill.rsps.entities.player;

public class LoginResult {
	
	public static final int WAIT_2S = -1;
	public static final int EXCHANGE_KEYS = 0;
	public static final int TRY_AGAIN = 1;
	public static final int SUCCESS = 2;
	public static final int INVALID_INFORMATION = 3;
	public static final int ACCOUNT_BANNED = 4;
	public static final int ALREADY_LOGGED_IN = 5;
	public static final int UPDATED = 6;
	public static final int WORLD_FULL = 7;
	public static final int SERVER_OFFLINE = 8;
	public static final int LOGIN_LIMIT = 9;
	public static final int BAD_SESSION_ID = 10;
	public static final int REJECTED = 11;
	public static final int MEMBERS_WORLD = 12;
	public static final int TRY_ANOTHER_WORLD = 13;
	public static final int UPDATE_IN_PROGRESS = 14;
	public static final int LAGGED_OUT = 15;
	public static final int TOO_MANY_ATTEMPTS = 16;
	public static final int MEMBERS_ONLY_AREA = 17;
	
	private Player p;
	private int opcode;
	
	public LoginResult(int opcode, Player p) {
		this.opcode = opcode;
		this.p = p;
	}

	public Player getPlayer() {
		return p;
	}

	public int getOpcode() {
		return opcode;
	}

}
