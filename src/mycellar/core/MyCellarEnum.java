package mycellar.core;

public class MyCellarEnum {
	  private final int value;
	  private final String label;
	  
	  public MyCellarEnum(int value, String label) {
		  this.value = value;
		  this.label = label;
	  }
	  public int getValue() {
			return value;
		}
		public String toString() {
			return label;
		}
	}
