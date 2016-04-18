<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;

class LithoStratigraphy extends NBADomainObject {

	private $qualifier;
	private $preferredFlag;
	private $member2;
	private $member;
	private $informalName2;
	private $informalName;
	private $importedName2;
	private $importedName1;
	private $lithoIdentifier;
	private $formation2;
	private $formationGroup2;
	private $formationGroup;
	private $formation;
	private $certainty2;
	private $certainty;
	private $bed2;
	private $bed;

	public function getQualifier()
	{
		return $this->qualifier;
	}

	public function setQualifier(string $qualifier)
	{
		$this->qualifier = $qualifier;
	}

	public function isPreferredFlag()
	{
		return $this->preferredFlag;
	}

	public function setPreferredFlag(bool $preferredFlag)
	{
		$this->preferredFlag = $preferredFlag;
	}

	public function getMember2()
	{
		return $this->member2;
	}

	public function setMember2(string $member2)
	{
		$this->member2 = $member2;
	}

	public function getMember()
	{
		return $this->member;
	}

	public function setMember(string $member)
	{
		$this->member = $member;
	}

	public function getInformalName2()
	{
		return $this->informalName2;
	}

	public function setInformalName2(string $informalName2)
	{
		$this->informalName2 = $informalName2;
	}

	public function getInformalName()
	{
		return $this->informalName;
	}

	public function setInformalName(string $informalName)
	{
		$this->informalName = $informalName;
	}

	public function getImportedName2()
	{
		return $this->importedName2;
	}

	public function setImportedName2(string $importedName2)
	{
		$this->importedName2 = $importedName2;
	}

	public function getImportedName1()
	{
		return $this->importedName1;
	}

	public function setImportedName1(string $importedName1)
	{
		$this->importedName1 = $importedName1;
	}

	public function getLithoIdentifier()
	{
		return $this->lithoIdentifier;
	}

	public function setLithoIdentifier(string $lithoIdentifier)
	{
		$this->lithoIdentifier = $lithoIdentifier;
	}

	public function getFormation2()
	{
		return $this->formation2;
	}

	public function setFormation2(string $formation2)
	{
		$this->formation2 = $formation2;
	}

	public function getFormationGroup2()
	{
		return $this->formationGroup2;
	}

	public function setFormationGroup2(string $formationGroup2)
	{
		$this->formationGroup2 = $formationGroup2;
	}

	public function getFormationGroup()
	{
		return $this->formationGroup;
	}

	public function setFormationGroup(string $formationGroup)
	{
		$this->formationGroup = $formationGroup;
	}

	public function getFormation()
	{
		return $this->formation;
	}

	public function setFormation(string $formation)
	{
		$this->formation = $formation;
	}

	public function getCertainty2()
	{
		return $this->certainty2;
	}

	public function setCertainty2(string $certainty2)
	{
		$this->certainty2 = $certainty2;
	}

	public function getCertainty()
	{
		return $this->certainty;
	}

	public function setCertainty(string $certainty)
	{
		$this->certainty = $certainty;
	}

	public function getBed2()
	{
		return $this->bed2;
	}

	public function setBed2(string $bed2)
	{
		$this->bed2 = $bed2;
	}

	public function getBed()
	{
		return $this->bed;
	}

	public function setBed(string $bed)
	{
		$this->bed = $bed;
	}
}
