<?php

namespace nl\naturalis\nba\client\php\model;

final class PhaseOrStage {

	private static $adult;
	private static $subadult;
	private static $egg;
	private static $embryo;
	private static $immature;
	private static $juvenile;
	private static $larva;
	private static $nymph;

	public static function ADULT()
	{
		if (self::$adult === null)
			self::$adult = new PhaseOrStage("adult");
		return self::$adult;
	}

	public static function SUBADULT()
	{
		if (self::$subadult === null)
			self::$subadult = new PhaseOrStage("subadult");
		return self::$subadult;
	}

	public static function EGG()
	{
		if (self::$egg === null)
			self::$egg = new PhaseOrStage("egg");
		return self::$egg;
	}

	public static function EMBRYO()
	{
		if (self::$embryo === null)
			self::$embryo = new PhaseOrStage("embryo");
		return self::$embryo;
	}

	public static function IMMATURE()
	{
		if (self::$immature === null)
			self::$immature = new PhaseOrStage("immature");
		return self::$immature;
	}

	public static function JUVENILE()
	{
		if (self::$juvenile === null)
			self::$juvenile = new PhaseOrStage("juvenile");
		return self::$juvenile;
	}

	public static function LARVA()
	{
		if (self::$larva === null)
			self::$larva = new PhaseOrStage("larva");
		return self::$larva;
	}

	public static function NYMPH()
	{
		if (self::$nymph === null)
			self::$nymph = new PhaseOrStage("nymph");
		return self::$nymph;
	}

	private final $name;

	private function __construct($name)
	{
		$this->name = $name;
	}

	public function __toString()
	{
		return $this->name;
	}
}
