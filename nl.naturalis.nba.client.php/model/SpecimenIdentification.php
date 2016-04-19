<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\TaxonomicIdentification;

class SpecimenIdentification extends TaxonomicIdentification {

	private $preferred;
	private $verificationStatus;
	private $rockType;
	private $associatedFossilAssemblage;
	private $rockMineralUsage;
	private $associatedMineralName;
	private $remarks;

	public function __construct()
	{
		parent::__construct();
	}

	public function functionisPreferred()
	{
		return $this->preferred;
	}


	public function functionsetPreferred(bool $preferred)
	{
		$this->preferred = $preferred;
	}


	public function functiongetVerificationStatus()
	{
		return $this->verificationStatus;
	}


	public function functionsetVerificationStatus(string $verificationStatus)
	{
		$this->verificationStatus = $verificationStatus;
	}


	public function functiongetRockType()
	{
		return $this->rockType;
	}


	public function functionsetRockType(string $rockType)
	{
		$this->rockType = $rockType;
	}


	public function functiongetAssociatedFossilAssemblage()
	{
		return $this->associatedFossilAssemblage;
	}


	public function functionsetAssociatedFossilAssemblage(string $associatedFossilAssemblage)
	{
		$this->associatedFossilAssemblage = $associatedFossilAssemblage;
	}


	public function functiongetRockMineralUsage()
	{
		return $this->rockMineralUsage;
	}


	public function functionsetRockMineralUsage(string $rockMineralUsage)
	{
		$this->rockMineralUsage = $rockMineralUsage;
	}


	public function functiongetAssociatedMineralName()
	{
		return $this->associatedMineralName;
	}


	public function functionsetAssociatedMineralName(string $associatedMineralName)
	{
		$this->associatedMineralName = $associatedMineralName;
	}


	public function functiongetRemarks()
	{
		return $this->remarks;
	}


	public function functionsetRemarks(string $remarks)
	{
		$this->remarks = $remarks;
	}


}
