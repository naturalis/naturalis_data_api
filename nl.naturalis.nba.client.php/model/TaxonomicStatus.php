<?php

namespace nl\naturalis\nba\client\php\model;

final class TaxonomicStatus {

	private static $acceptedName;
	private static $synonym;
	private static $basionym;
	private static $homonym;
	private static $ambiguousSynonym;
	private static $misappliedName;
	private static $misspelledName;
	private static $provisionallyAccepted;

	public static function ACCEPTED_NAME()
	{
		if (self::$acceptedName === null)
			self::$acceptedName = new TaxonomicStatus("accepted name");
		return self::$acceptedName;
	}

	public static function SYNONYM()
	{
		if (self::$synonym === null)
			self::$synonym = new TaxonomicStatus("synonym");
		return self::$synonym;
	}

	public static function BASIONYM()
	{
		if (self::$basionym === null)
			self::$basionym = new TaxonomicStatus("basionym");
		return self::$basionym;
	}

	public static function HOMONYM()
	{
		if (self::$homonym === null)
			self::$homonym = new TaxonomicStatus("homonym");
		return self::$homonym;
	}

	public static function AMBIGUOUS_SYNONYM()
	{
		if (self::$ambiguousSynonym === null) {
			self::$ambiguousSynonym = new TaxonomicStatus("ambiguous synonym");
		}
		return new TaxonomicStatus("ambiguous synonym");
	}

	public static function MISAPPLIED_NAME()
	{
		if (self::$misappliedName === null)
			self::$misappliedName = new TaxonomicStatus("misapplied name");
		return self::$misappliedName;
	}

	public static function MISSPELLED_NAME()
	{
		if (self::$misspelledName === null)
			self::$misspelledName = new TaxonomicStatus("misapplied name");
		return self::$misspelledName;
	}

	public static function PROVISIONALLY_ACCEPTED()
	{
		if (self::$provisionallyAccepted === null)
			self::$provisionallyAccepted = new TaxonomicStatus("provisionally accepted name");
		return self::$provisionallyAccepted;
	}

	private final $name;

	private function __construct(string $name)
	{
		$this->name = $name;
	}

	public function __toString()
	{
		return name;
	}
}