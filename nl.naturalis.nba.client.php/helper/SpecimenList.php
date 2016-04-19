<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\Specimen;

class SpecimenList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\Specimen
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(Specimen $element)
	{
		$this->data[] = $element;
	}
}
