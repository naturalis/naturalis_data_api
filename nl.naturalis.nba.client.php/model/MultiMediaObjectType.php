<?php

namespace nl\naturalis\nba\client\php\model;

final class MultiMediaObjectType {

	private static $collection;
	private static $stillImage;
	private static $sound;
	private static $movingImage;
	private static $interactiveResource;
	private static $text;
	private static $other;

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\MultiMediaObjectType
	 */
	public static function COLLECTION()
	{
		if (self::$collection === null) {
			self::$collection = new MultiMediaObjectType(__METHOD__);
		}
		return self::$collection;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\MultiMediaObjectType
	 */
	public static function STILL_IMAGE()
	{
		if (self::$stillImage === null) {
			self::$stillImage = new MultiMediaObjectType(__METHOD__);
		}
		return self::$stillImage;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\MultiMediaObjectType
	 */
	public static function SOUND()
	{
		if (self::$sound === null) {
			self::$sound = new MultiMediaObjectType(__METHOD__);
		}
		return self::$sound;
	}

	public static function MOVING_IMAGE()
	{
		if (self::$movingImage === null) {
			self::$movingImage = new MultiMediaObjectType(__METHOD__);
		}
		return self::$movingImage;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\MultiMediaObjectType
	 */
	public static function INTERACTIVE_RESOURCE()
	{
		if (self::$interactiveResource === null) {
			self::$interactiveResource = new MultiMediaObjectType(__METHOD__);
		}
		return self::$interactiveResource;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\MultiMediaObjectType
	 */
	public static function TEXT()
	{
		if (self::$text === null) {
			self::$text = new MultiMediaObjectType(__METHOD__);
		}
		return self::$text;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\MultiMediaObjectType
	 */
	public static function OTHER()
	{
		if (self::$other === null) {
			self::$other = new MultiMediaObjectType(__METHOD__);
		}
		return self::$other;
	}

	private final $name;

	private function __construct(string $name)
	{
		$this->name = $name;
	}
}
