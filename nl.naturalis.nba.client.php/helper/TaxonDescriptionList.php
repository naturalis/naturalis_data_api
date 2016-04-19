<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\TaxonDescription;

class TaxonDescriptionList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return TaxonDescription
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(TaxonDescription $element)
	{
		$this->data[] = $element;
	}
}
