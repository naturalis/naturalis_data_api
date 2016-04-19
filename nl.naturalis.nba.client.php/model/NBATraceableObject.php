<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;
use nl\naturalis\nba\client\php\model\SourceSystem;

abstract class NBATraceableObject extends NBADomainObject {

	private $sourceSystem;
	private $sourceSystemId;
	private $recordURI;

	public function __construct()
	{
		parent::__construct();
	}

	/**
	 *
	 * @return string
	 */
	public function getSourceSystem()
	{
		return sourceSystem;
	}

	/**
	 *
	 * @param SourceSystem $sourceSystem
	 */
	public function setSourceSystem(SourceSystem $sourceSystem)
	{
		$this->sourceSystem = $sourceSystem;
	}

	/**
	 *
	 * @return string
	 */
	public function getSourceSystemId()
	{
		return sourceSystemId;
	}

	/**
	 *
	 * @param string $sourceSystemId
	 */
	public function setSourceSystemId(string $sourceSystemId)
	{
		$this->sourceSystemId = $sourceSystemId;
	}

	/**
	 *
	 * @return string
	 */
	public function getRecordURI()
	{
		return recordURI;
	}

	/**
	 *
	 * @param string $recordURI
	 */
	public function setRecordURI(string $recordURI)
	{
		$this->recordURI = $recordURI;
	}
}
