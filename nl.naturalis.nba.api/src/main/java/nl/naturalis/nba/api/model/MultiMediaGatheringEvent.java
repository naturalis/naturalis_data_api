package nl.naturalis.nba.api.model;

public class MultiMediaGatheringEvent extends GatheringEvent {

	public Iptc4xmpExt getIptc()
	{
		Iptc4xmpExt iptc = new Iptc4xmpExt();
		iptc.setCity(getCity());
		iptc.setCountryCode(getIso3166Code());
		iptc.setCountryName(getCountry());
		String location = getLocalityText();
		if (location == null) {
			location = getCity();
		}
		if (location == null) {
			location = getLocality();
		}
		if (location == null) {
			location = getIsland();
		}
		if (location == null) {
			location = getCountry();
		}
		if (location == null) {
			location = getContinent();
		}
		if (location == null) {
			location = getWorldRegion();
		}
		iptc.setLocationShown(location);
		iptc.setProvinceState(getProvinceState());
		iptc.setWorldRegion(getWorldRegion());
		return iptc;
	}

}
