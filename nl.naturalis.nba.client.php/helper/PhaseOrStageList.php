<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\PhaseOrStage;

class PhaseOrStageList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\PhaseOrStage
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(PhaseOrStage $element)
	{
		$this->data[] = $element;
	}
}
