<?php

namespace nl\naturalis\nba\client\php\model;

final class TaxonomicRank {

	private static $kingdom;
	private static $phylum;
	private static $classis;
	private static $order;
	private static $superFamily;
	private static $family;
	private static $tribe;
	private static $genus;
	private static $subgenus;
	private static $species;
	private static $subspecies;

	public static function KINGDOM()
	{
		if (self::$kingdom === null)
			self::$kingdom = new TaxonomicRank("kingdom", "regnum");
		return self::$kingdom;
	}

	public static function PHYLUM()
	{
		if (self::$phylum === null)
			self::$phylum = new TaxonomicRank("phylum");
		return self::$phylum;
	}

	public static function CLASSIS()
	{
		if (self::$classis === null)
			self::$classis = new TaxonomicRank("class", "classis");
		return self::$classis;
	}

	public static function ORDER()
	{
		if (self::$order === null)
			self::$order = new TaxonomicRank("order", "ordo");
		return self::$order;
	}

	public static function SUPER_FAMILY()
	{
		if (self::$superFamily === null)
			self::$superFamily = new TaxonomicRank("superfamily", "suprafamilia");
		return self::$superFamily;
	}

	public static function FAMILY()
	{
		if (self::$family === null)
			self::$family = new TaxonomicRank("family", "familia");
		return self::$family;
	}

	public static function TRIBE()
	{
		if (self::$tribe === null)
			self::$tribe = new TaxonomicRank("tribe", "tribus");
		return self::$tribe;
	}

	public static function GENUS()
	{
		if (self::$genus === null)
			self::$genus = new TaxonomicRank("genus");
		return self::$genus;
	}

	public static function SUBGENUS()
	{
		if (self::$subgenus === null)
			self::$subgenus = new TaxonomicRank("subgenus");
		return self::$subgenus;
	}

	public static function SPECIES()
	{
		if (self::$species === null)
			self::$species = new TaxonomicRank("species");
		return self::$species;
	}

	public static function SUBSPECIES()
	{
		if (self::$subspecies === null)
			self::$subspecies = new TaxonomicRank("subspecies");
		return self::$subspecies;
	}

	private final $englishName;
	private final $latinName;

	private function __construct(String $englishName, String $latinName)
	{
		$this->englishName = $englishName;
		if (isset($latinName))
			$this->latinName = $latinName;
		else
			$this->latinName = $englishName;
	}

	public function getEnglishName()
	{
		return $this->englishName;
	}

	public function getLatinName()
	{
		return $this->latinName;
	}

	public function __toString()
	{
		return $this->englishName;
	}
}