<?php

namespace nl\naturalis\nba\client\php\helper;

abstract class AbstractList {

	protected $data;

	public function __construct(array $data)
	{
		$this->data = isset($data) ? $data : array();
	}

	public function size()
	{
		return count($data);
	}
}
