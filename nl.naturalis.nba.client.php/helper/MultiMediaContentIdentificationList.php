<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\MultiMediaContentIdentification;

class MultiMediaContentIdentificationList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\MultiMediaContentIdentification
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(MultiMediaContentIdentification $element)
	{
		$this->data[] = $element;
	}
}
