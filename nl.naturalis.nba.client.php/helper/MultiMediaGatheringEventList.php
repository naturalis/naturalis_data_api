<?php

namespace nl\naturalis\nba\client\php\helper;

use nl\naturalis\nba\client\php\helper\AbstractList;
use nl\naturalis\nba\client\php\model\MultiMediaGatheringEvent;

class MultiMediaGatheringEventList extends AbstractList {

	public function __construct(array $data)
	{
		parent::__construct($data);
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\MultiMediaGatheringEvent
	 */
	public function get(int $index)
	{
		return $this->data[$index];
	}

	public function add(MultiMediaGatheringEvent $element)
	{
		$this->data[] = $element;
	}
}
