package nl.naturalis.nba.rest.resource;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class Registry {
}
