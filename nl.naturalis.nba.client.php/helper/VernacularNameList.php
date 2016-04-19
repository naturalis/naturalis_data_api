<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\VernacularName;

class VernacularNameList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return VernacularName
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(VernacularName $element)
	{
		$this->data[] = $element;
	}
}
