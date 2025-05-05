package es.caib.notib.client.domini.explotacio;

public enum DiaSetmanaEnum {
	DL,
	DM,
	DC,
	DJ,
	DV,
	DS,
	DG;

	public static DiaSetmanaEnum valueOfData(String dayOfWeek) {
		switch (dayOfWeek) {
			case "MONDAY": return DL;
			case "TUESDAY": return DM;
			case "WEDNESDAY": return DC;
			case "THURSDAY": return DJ;
			case "FRIDAY": return DV;
			case "SATURDAY": return DS;
			case "SUNDAY": return DG;
			default: return null;
		}
	}
}
