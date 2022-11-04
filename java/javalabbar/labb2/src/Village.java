public class Village {
    final int SIZE = 1000;
    Person[] population = new Person[SIZE];

    public Village(boolean isVaccinated, boolean covid19) {
        for (int i = 0; i < SIZE; i = i + 1) {
            population[i] = new Person(isVaccinated, covid19);
        }

    }

    public int countDead() {
        int dead = 0;
        for (Person person : population) {
            if (person.isDead) {
                dead = dead + 1;
            }
        }
        return dead;
    }

    public int countSick() {
        int sick = 0;
        for (Person person : population) {
            if (person.isSick) {
                sick = sick + 1;
            }
        }
        return sick;
    }

    public void dayPassesAll() {
        for (Person person : population) {
            person.dayPasses(population);
        }
    }
}
