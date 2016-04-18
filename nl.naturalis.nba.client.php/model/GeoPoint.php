<?php

namespace nl\naturalis\nba\client\php\model;

class GeoPoint extends GeoShape {

	private $coordinates;

	// TODO
	// public GeoPoint()
	// {
	// this.type = Type.POINT;
	// }

	// public GeoPoint(double longitude, double latitude)
	// {
	// this.type = Type.POINT;
	// this.coordinates = new double[] { longitude, latitude };
	// }

	public function getCoordinates()
	{
		return $this->coordinates;
	}

	public function setCoordinates(array $coordinates)
	{
		$this->coordinates = $coordinates;
	}
}
