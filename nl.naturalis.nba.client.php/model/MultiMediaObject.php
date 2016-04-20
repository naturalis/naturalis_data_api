<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\helper\MultiMediaGatheringEventList;
use nl\naturalis\nba\client\php\helper\PhaseOrStageList;
use nl\naturalis\nba\client\php\helper\ServiceAccessPointList;
use nl\naturalis\nba\client\php\helper\SexList;
use nl\naturalis\nba\client\php\model\SpecimenTypeStatus;
use nl\naturalis\nba\client\php\helper\MultiMediaContentIdentificationList;

class MultiMediaObject extends NBATraceableObject {

	private $sourceInstitutionID;
	private $sourceID;
	private $owner;
	private $licenseType;
	private $license;
	private $unitID;
	private $collectionType;
	private $title;
	private $caption;
	private $description;
	private $serviceAccessPoints;
	private $type;
	private $taxonCount;
	private $creator;
	private $copyrightText;
	private $associatedSpecimenReference;
	private $associatedTaxonReference;
	private $specimenTypeStatus;
	private $multimediaPublic;
	private $subjectParts;
	private $subjectOrientations;
	private $phasesOrStages;
	private $sexes;
	private $gatheringEvents;
	private $identifications;
	private $associatedSpecimen;
	private $associatedTaxon;

	public function getSourceInstitutionID()
	{
		return $this->sourceInstitutionID;
	}

	public function setSourceInstitutionID(string $sourceInstitutionID)
	{
		$this->sourceInstitutionID = sourceInstitutionID;
	}

	public function getSourceID()
	{
		return $this->sourceID;
	}

	public function setSourceID(string $sourceID)
	{
		$this->sourceID = sourceID;
	}

	public function getOwner()
	{
		return $this->owner;
	}

	public function setOwner(string $owner)
	{
		$this->owner = owner;
	}

	public function getLicenseType()
	{
		return $this->licenseType;
	}

	public function setLicenseType(string $licenseType)
	{
		$this->licenseType = licenseType;
	}

	public function getLicense()
	{
		return $this->license;
	}

	public function setLicense(string $license)
	{
		$this->license = license;
	}

	public function getUnitID()
	{
		return $this->unitID;
	}

	public function setUnitID(string $unitID)
	{
		$this->unitID = unitID;
	}

	public function getCollectionType()
	{
		return $this->collectionType;
	}

	public function setCollectionType(string $collectionType)
	{
		$this->collectionType = collectionType;
	}

	public function getTitle()
	{
		return $this->title;
	}

	public function setTitle(string $title)
	{
		$this->title = title;
	}

	public function getCaption()
	{
		return $this->caption;
	}

	public function setCaption(string $caption)
	{
		$this->caption = caption;
	}

	public function getDescription()
	{
		return $this->description;
	}

	public function setDescription(string $description)
	{
		$this->description = description;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\ServiceAccessPointList
	 */
	public function getServiceAccessPoints()
	{
		return $this->serviceAccessPoints;
	}

	public function setServiceAccessPoints(ServiceAccessPointList $serviceAccessPoints)
	{
		$this->serviceAccessPoints = serviceAccessPoints;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\MultiMediaObjectType
	 */
	public function getType()
	{
		return $this->type;
	}

	public function setType(MultiMediaObjectType $type)
	{
		$this->type = $type;
	}

	public function getTaxonCount()
	{
		return $this->taxonCount;
	}

	public function setTaxonCount(int $taxonCount)
	{
		$this->taxonCount = taxonCount;
	}

	public function getCreator()
	{
		return $this->creator;
	}

	public function setCreator(string $creator)
	{
		$this->creator = creator;
	}

	public function getCopyrightText()
	{
		return $this->copyrightText;
	}

	public function setCopyrightText(string $copyrightText)
	{
		$this->copyrightText = copyrightText;
	}

	public function getAssociatedSpecimenReference()
	{
		return $this->associatedSpecimenReference;
	}

	public function setAssociatedSpecimenReference(string $associatedSpecimenReference)
	{
		$this->associatedSpecimenReference = associatedSpecimenReference;
	}

	public function getAssociatedTaxonReference()
	{
		return $this->associatedTaxonReference;
	}

	public function setAssociatedTaxonReference(string $associatedTaxonReference)
	{
		$this->associatedTaxonReference = associatedTaxonReference;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\SpecimenTypeStatus
	 */
	public function getSpecimenTypeStatus()
	{
		return $this->specimenTypeStatus;
	}

	public function setSpecimenTypeStatus(SpecimenTypeStatus $specimenTypeStatus)
	{
		$this->specimenTypeStatus = $specimenTypeStatus;
	}

	public function isMultimediaPublic()
	{
		return $this->multimediaPublic;
	}

	public function setMultimediaPublic(bool $multimediaPublic)
	{
		$this->multimediaPublic = $multimediaPublic;
	}

	public function getSubjectParts()
	{
		return $this->subjectParts;
	}

	public function setSubjectParts(array $subjectParts)
	{
		$this->subjectParts = subjectParts;
	}

	public function getSubjectOrientations()
	{
		return $this->subjectOrientations;
	}

	public function setSubjectOrientations(array $subjectOrientations)
	{
		$this->subjectOrientations = subjectOrientations;
	}

	public function getPhasesOrStages()
	{
		return $this->phasesOrStages;
	}

	public function setPhasesOrStages(PhaseOrStageList $phasesOrStages)
	{
		$this->phasesOrStages = $phasesOrStages;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\helper\SexList
	 */
	public function getSexes()
	{
		return $this->sexes;
	}

	public function setSexes(SexList $sexes)
	{
		$this->sexes = $sexes;
	}

	public function getGatheringEvents()
	{
		return $this->gatheringEvents;
	}

	public function setGatheringEvents(MultiMediaGatheringEventList $gatheringEvents)
	{
		$this->gatheringEvents = $gatheringEvents;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\MultiMediaContentIdentificationList
	 */
	public function getIdentifications()
	{
		return $this->identifications;
	}

	public function setIdentifications(MultiMediaContentIdentificationList $identifications)
	{
		$this->identifications = identifications;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\Specimen
	 */
	public function getAssociatedSpecimen()
	{
		return $this->associatedSpecimen;
	}

	public function setAssociatedSpecimen(Specimen $associatedSpecimen)
	{
		$this->associatedSpecimen = $associatedSpecimen;
	}

	public function getAssociatedTaxon()
	{
		return $this->associatedTaxon;
	}

	public function setAssociatedTaxon(Taxon $associatedTaxon)
	{
		$this->associatedTaxon = $associatedTaxon;
	}
}
