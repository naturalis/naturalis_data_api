<?php

namespace nl\naturalis\nba\client\php\model;

class Sex {

	private static $male;
	private static $female;
	private static $mixed;
	private static $hermaphrodite;

	public static function MALE()
	{
		if (self::$male === null)
			self::$male = new Sex("male");
		return self::$male;
	}

	public static function FEMALE()
	{
		if (self::$female === null)
			self::$female = new Sex("female");
		return self::$female;
	}

	public static function HERMAPHRODITE()
	{
		if (self::$hermaphrodite === null)
			self::$hermaphrodite = new Sex("hermaphrodite");
		return self::$hermaphrodite;
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
