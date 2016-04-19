<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\model\Reference;
use nl\naturalis\nba\client\php\helper\AbstractList;

class ReferenceList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return Reference
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(Reference $element)
	{
		$this->data[] = $element;
	}
}
