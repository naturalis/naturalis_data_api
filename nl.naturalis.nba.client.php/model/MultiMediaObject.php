<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\ServiceAccessPoint;
use nl\naturalis\nba\client\php\model\ServiceAccessPointVariant;
use nl\naturalis\nba\client\php\model\SpecimenTypeStatus;

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


	public function addServiceAccessPoint(string $uri, string $format, ServiceAccessPointVariant $variant)
	{
		if ($this->serviceAccessPoints == null) {
			$this->serviceAccessPoints = array();
		}
		$key = $variant->__toString();
		$val = new ServiceAccessPoint($uri, $format, $variant);
		$this->serviceAccessPoints[$key] = $val;
	}


	public function addServiceAccessPoint(ServiceAccessPoint $sap)
	{
		if ($this->serviceAccessPoints == null) {
			$this->serviceAccessPoints = array();
		}
		$key = $sap->getVariant()->__toString();
		$val = $sap->getVariant();
		$this->serviceAccessPoints[$key] = $val;
	}


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


	public Map<ServiceAccessPoint.Variant, ServiceAccessPoint> getServiceAccessPoints()
	{
		return $this->serviceAccessPoints;
	}


	public function setServiceAccessPoints(Map<ServiceAccessPoint.Variant, ServiceAccessPoint> serviceAccessPoints)
	{
		$this->serviceAccessPoints = serviceAccessPoints;
	}


	public Type getType()
	{
		return $this->type;
	}


	public function setType(Type type)
	{
		$this->type = type;
	}


	public int getTaxonCount()
	{
		return $this->taxonCount;
	}


	public function setTaxonCount(int taxonCount)
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


	public SpecimenTypeStatus getSpecimenTypeStatus()
	{
		return $this->specimenTypeStatus;
	}


	public function setSpecimenTypeStatus(SpecimenTypeStatus specimenTypeStatus)
	{
		$this->specimenTypeStatus = specimenTypeStatus;
	}


	public function isMultimediaPublic()
	{
		return $this->multimediaPublic;
	}


	public function setMultimediaPublic(bool $multimediaPublic)
	{
		$this->multimediaPublic = multimediaPublic;
	}


	public function getSubjectParts()
	{
		return $this->subjectParts;
	}


	public function setSubjectParts(array subjectParts)
	{
		$this->subjectParts = subjectParts;
	}


	public function getSubjectOrientations()
	{
		return $this->subjectOrientations;
	}


	public function setSubjectOrientations(List<String> subjectOrientations)
	{
		$this->subjectOrientations = subjectOrientations;
	}


	public function getPhasesOrStages()
	{
		return $this->phasesOrStages;
	}


	public function setPhasesOrStages(List<String> phasesOrStages)
	{
		$this->phasesOrStages = phasesOrStages;
	}


	public function getSexes()
	{
		return $this->sexes;
	}


	public function setSexes(List<String> sexes)
	{
		$this->sexes = sexes;
	}


	public List<MultiMediaGatheringEvent> getGatheringEvents()
	{
		return $this->gatheringEvents;
	}


	public function setGatheringEvents(List<MultiMediaGatheringEvent> gatheringEvents)
	{
		$this->gatheringEvents = gatheringEvents;
	}


	public function getIdentifications()
	{
		return $this->identifications;
	}


	public function setIdentifications(array $identifications)
	{
		$this->identifications = identifications;
	}


	public function getAssociatedSpecimen()
	{
		return $this->associatedSpecimen;
	}


	public function setAssociatedSpecimen(Specimen associatedSpecimen)
	{
		$this->associatedSpecimen = associatedSpecimen;
	}


	public function getAssociatedTaxon()
	{
		return $this->associatedTaxon;
	}


	public function setAssociatedTaxon(Taxon associatedTaxon)
	{
		$this->associatedTaxon = associatedTaxon;
	}

}
