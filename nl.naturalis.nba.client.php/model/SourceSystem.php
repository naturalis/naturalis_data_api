<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;

final class SourceSystem extends NBADomainObject {

	private static $brahms;
	private static $col;
	private static $crs;
	private static $nsr;
	private static $ndff;

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SourceSystem
	 */
	public static function BRAHMS()
	{
		if (self::$brahms === null)
			self::$brahms = new SourceSystem("BRAHMS", "Naturalis - Botany catalogues");
		return self::$brahms;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SourceSystem
	 */
	public static function COL()
	{
		if (self::$col === null)
			self::$col = new SourceSystem("COL", "Species 2000 - Catalogue Of Life");
		return self::$col;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SourceSystem
	 */
	public static function CRS()
	{
		if (self::$crs === null)
			self::$crs = new SourceSystem("CRS", "Naturalis - Zoology and Geology catalogues");
		return self::$crs;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SourceSystem
	 */
	public static function NSR()
	{
		if (self::$nsr === null)
			self::$nsr = new SourceSystem("NSR", "Naturalis - Nederlands Soortenregister");
		return self::$nsr;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SourceSystem
	 */
	public static function NDFF()
	{
		if (self::$ndff === null)
			self::$ndff = new SourceSystem("NDFF", "NDFF - Nationale Databank Flora en Fauna");
		return self::$ndff;
	}

	private final $code;
	private final $name;

	private function __construct(string $code, string $name)
	{
		$this->code = $code;
		$this->name = $name;
	}

	public function getCode()
	{
		return code;
	}

	public function setCode(string $code)
	{
		$this->code = $code;
	}

	public function getName()
	{
		return name;
	}

	public function setName(string $name)
	{
		$this->name = $name;
	}
}
