<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\Taxon;

class TaxonList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\Taxon
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(Taxon $element)
	{
		$this->data[] = $element;
	}
}
