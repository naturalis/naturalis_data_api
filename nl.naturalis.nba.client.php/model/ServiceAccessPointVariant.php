<?php

namespace nl\naturalis\nba\client\php\model;

final class ServiceAccessPointVariant {

	private static $thumbnail;
	private static $trailer;
	private static $lowerQuality;
	private static $mediumQuality;
	private static $goodQuality;
	private static $bestQuality;
	private static $offline;

	public static function THUMBNAIL()
	{
		if (self::$thumbnail === null)
			self::$thumbnail = new ServiceAccessPointVariant(__METHOD__);
		return self::$thumbnail;
	}

	public static function TRAILER()
	{
		if (self::$trailer === null)
			self::$trailer = new ServiceAccessPointVariant(__METHOD__);
		return self::$trailer;
	}

	public static function LOWER_QUALITY()
	{
		if (self::$lowerQuality === null)
			self::$lowerQuality = new ServiceAccessPointVariant(__METHOD__);
		return self::$lowerQuality;
	}

	public static function MEDIUM_QUALITY()
	{
		if (self::$mediumQuality === null)
			self::$mediumQuality = new ServiceAccessPointVariant(__METHOD__);
		return self::$mediumQuality;
	}

	public static function GOOD_QUALITY()
	{
		if (self::$goodQuality === null)
			self::$goodQuality = new ServiceAccessPointVariant(__METHOD__);
		return self::$goodQuality;
	}

	public static function BEST_QUALITY()
	{
		if (self::$bestQuality === null)
			self::$bestQuality = new ServiceAccessPointVariant(__METHOD__);
		return self::$bestQuality;
	}

	public static function OFFLINE()
	{
		if (self::$offline === null)
			self::$offline = new ServiceAccessPointVariant(__METHOD__);
		return self::$offline;
	}

	private final $name;

	private function __construct(string $name)
	{
		$this->name = $name;
	}

	public function __toString()
	{
		return $this->name;
	}
}
