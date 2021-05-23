package mycellar.core.common.bottle;

import mycellar.Program;

public enum BottleColor {
	NONE(""),
	RED(Program.getLabel("BottleColor.red")),
	PINK(Program.getLabel("BottleColor.pink")),
	WHITE(Program.getLabel("BottleColor.white"));
	
	private final String label;
	
	BottleColor(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	public static BottleColor getColor(String value) {
		if(value.isEmpty()) {
			return NONE;
		}
		try {
			return valueOf(value);
		}catch(Exception e) {
			if(value.equals(RED.label)) {
				return RED;
			}	else if(value.equals(WHITE.label)) {
				return WHITE;
			} else if(value.equals(PINK.label)) {
				return PINK;
			}
		}
		return NONE;
	}

}
