<?php

namespace nl\naturalis\nba\client\php\model;

use \DateTime;
use nl\naturalis\nba\client\php\model\NBADomainObject;
use nl\naturalis\nba\client\php\model\Person;

class Reference extends NBADomainObject {

	private $titleCitation;
	private $citationDetail;
	private $uri;
	private $author;
	private $publicationDate;

	public function getTitleCitation()
	{
		return titleCitation;
	}

	public function setTitleCitation(string $titleCitation)
	{
		$this->titleCitation = $titleCitation;
	}

	public function getCitationDetail()
	{
		return citationDetail;
	}

	public function setCitationDetail(string $citationDetail)
	{
		$this->citationDetail = $citationDetail;
	}

	public function getUri()
	{
		return uri;
	}

	public function setUri(string $uri)
	{
		$this->uri = $uri;
	}

	/**
	 *
	 * @return Person
	 */
	public function getAuthor()
	{
		return author;
	}

	public function setAuthor(Person $author)
	{
		$this->author = $author;
	}

	/**
	 *
	 * @return DateTime
	 */
	public function getPublicationDate()
	{
		return publicationDate;
	}

	public function setPublicationDate(DateTime $publicationDate)
	{
		$this->publicationDate = $publicationDate;
	}
}
