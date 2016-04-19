<?php

namespace nl\naturalis\nba\client\php\model;

final class SpecimenTypeStatus {

	private static $allotype;
	private static $epitype;
	private static $hapantotype;
	private static $holotype;
	private static $isoepitype;
	private static $isolectotype;
	private static $isoneotype;
	private static $isosyntype;
	private static $isotype;
	private static $lectotype;
	private static $neotype;
	private static $paratype;
	private static $paralectotype;
	private static $syntype;
	private static $topotype;
	private static $type;

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function ALLOTYPE()
	{
		if (self::$allotype === null)
			self::$allotype = new SpecimenTypeStatus("allotype");
		return self::$allotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function EPITYPE()
	{
		if (self::$epitype === null)
			self::$epitype = new SpecimenTypeStatus("epitype");
		return self::$epitype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function HAPANTOTYPE()
	{
		if (self::$hapantotype === null)
			self::$hapantotype = new SpecimenTypeStatus("hapantotype");
		return self::$hapantotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function HOLOTYPE()
	{
		if (self::$holotype === null)
			self::$holotype = new SpecimenTypeStatus("holotype");
		return self::$holotype;
	}

	public static function ISOEPITYPE()
	{
		if (self::$isoepitype === null)
			self::$isoepitype = new SpecimenTypeStatus("isoepitype");
		return self::$isoepitype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function ISOLECTOTYPE()
	{
		if (self::$isolectotype === null)
			self::$isolectotype = new SpecimenTypeStatus("isolectotype");
		return self::$isolectotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function ISONEOTYPE()
	{
		if (self::$isoneotype === null)
			self::$isoneotype = new SpecimenTypeStatus("isoneotype");
		return self::$isoneotype;
	}

	public static function ISOSYNTYPE()
	{
		if (self::$isosyntype === null)
			self::$isosyntype = new SpecimenTypeStatus("isosyntype");
		return self::$isosyntype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function ISOTYPE()
	{
		if (self::$isotype === null)
			self::$isotype = new SpecimenTypeStatus("isotype");
		return self::$isotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function LECTOTYPE()
	{
		if (self::$lectotype === null)
			self::$lectotype = new SpecimenTypeStatus("lectotype");
		return self::$lectotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function NEOTYPE()
	{
		if (self::$neotype === null)
			self::$neotype = new SpecimenTypeStatus("neotype");
		return self::$neotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function PARATYPE()
	{
		if (self::$paratype === null)
			self::$paratype = new SpecimenTypeStatus("paratype");
		return self::$paratype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function PARALECTOTYPE()
	{
		if (self::$paralectotype === null)
			self::$paralectotype = new SpecimenTypeStatus("paralectotype");
		return self::$paralectotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function SYNTYPE()
	{
		if (self::$syntype === null)
			self::$syntype = new SpecimenTypeStatus("syntype");
		return self::$syntype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function TOPOTYPE()
	{
		if (self::$topotype === null)
			self::$topotype = new SpecimenTypeStatus("topotype");
		return self::$topotype;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public static function TYPE()
	{
		if (self::$type === null)
			self::$type = new SpecimenTypeStatus("type");
		return self::$type;
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
