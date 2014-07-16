package nl.naturalis.nda.ejb.service;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class SpecimenService {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SpecimenService.class);


	public String getSpecimens()
	{
		return null;
	}

}
