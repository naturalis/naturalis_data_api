<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\Expert;

class ExpertList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return Expert
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(Expert $element)
	{
		$this->data[] = $element;
	}
}
