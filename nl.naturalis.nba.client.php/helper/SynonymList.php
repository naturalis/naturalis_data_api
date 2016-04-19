<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\Synonym;

class SynonymList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\Synonym
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(Synonym $element)
	{
		$this->data[] = $element;
	}
}
