<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;
use nl\naturalis\nba\client\php\model\ServiceAccessPointVariant;

class ServiceAccessPoint extends NBADomainObject {

	private $accessUri;
	private $format;
	private $variant;

	public function __construct(string $uri, string $format, ServiceAccessPointVariant $variant)
	{
		$this->accessUri = $uri;
		$this->format = $format;
		$this->variant = $variant;
	}

	public function getAccessUri()
	{
		return $this->accessUri;
	}

	public function setAccessUri(string $accessUri)
	{
		$this->accessUri = accessUri;
	}

	public function getFormat()
	{
		return $this->format;
	}

	public function setFormat(string $format)
	{
		$this->format = format;
	}

	public function getVariant()
	{
		return $this->variant;
	}

	public function setVariant(ServiceAccessPointVariant $variant)
	{
		$this->variant = variant;
	}

}
