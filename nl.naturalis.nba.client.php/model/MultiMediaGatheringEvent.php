<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\GatheringEvent;
use nl\naturalis\nba\client\php\model\Iptc4xmpExt;

class MultiMediaGatheringEvent extends GatheringEvent {


	private $iptc;


	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\Iptc4xmpExt
	 */
	public function getIptc()
	{
		if ($iptc === null) {
			$iptc = new Iptc4xmpExt();
			$iptc.setCity($this->getCity());
			$iptc.setCountryCode($this->getIso3166Code());
			$iptc.setCountryName($this->getCountry());
			$location = $this->getLocalityText();
			if ($location === null) {
				$location = $this->getCity();
			}
			if ($location === null) {
				$location = $this->getLocality();
			}
			if ($location === null) {
				$location = $this->getIsland();
			}
			if ($location === null) {
				$location = $this->getCountry();
			}
			if ($location === null) {
				$location = $this->getContinent();
			}
			if ($location === null) {
				$location = $this->getWorldRegion();
			}
			$iptc.setLocationShown($location);
			$iptc.setProvinceState($this->getProvinceState());
			$iptc.setWorldRegion($this->getWorldRegion());
		}
		return $iptc;
	}

}
