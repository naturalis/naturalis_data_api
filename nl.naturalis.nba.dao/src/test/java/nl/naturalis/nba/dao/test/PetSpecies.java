package nl.naturalis.nba.dao.test;

public enum PetSpecies
{
	DOG, CAT, PARROT, GOLD_FISH;

	public String toString()
	{
		switch (this) {
			case CAT:
				return "Cat";
			case DOG:
				return "Dog";
			case GOLD_FISH:
				return "Gold fish";
			case PARROT:
				return "Parrot";
		}
		return null;
	}
}
