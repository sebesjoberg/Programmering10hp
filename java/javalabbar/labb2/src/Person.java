import java.util.Random;
/**
 *
 * @author Sebastian Sjöberg
 */
public class Person {
    boolean isSick;
    final double INIT_SICK_PROB = 0.1;
    final double GET_WELL_PROB = 0.2;
    final double DIE_PROB = 0.002;
    double INFECT_PROB = 0.002;
    double xPos, yPos;
    double range = 100;
    final double DISTANCING = 0;
    final int DAYS_IMMUNE = 1000;
    int daysLeftImmune = 0;
    boolean isDead = false;
    final double vaccinationEfficency = 10.0;
    final double vaccinationProbability = 0.8;
    int daysToEvent;
    final int EVENT_PERIOD = 10;

    public Person(boolean isVaccinated, boolean covid19) {
        this.daysToEvent = this.EVENT_PERIOD;
        if (this.INIT_SICK_PROB >= Math.random() && !covid19) {
            this.isSick = true;
        }

        if (isVaccinated && this.vaccinationProbability >= Math.random()) {
            this.INFECT_PROB = this.INFECT_PROB / this.vaccinationEfficency;

        }
        Random r = new Random();
        this.xPos = r.nextInt(1001);
        this.yPos = r.nextInt(1001);
        if (covid19 && this.xPos >= 400 && this.xPos <= 600 && this.yPos >= 400 && this.yPos <= 600) {
            this.isSick = true;
        }
        if (covid19 && this.DISTANCING >= Math.random()) {
            this.range = this.range / 2;
        }
    }

    public void dayPasses(Person[] allPersons) {
        if (this.daysToEvent == 0) {
            this.range = 300;
            this.daysToEvent = this.EVENT_PERIOD;
        } else {
            this.range = 100;
            this.daysToEvent = this.daysToEvent - 1;
        }
        if (this.isSick) {
            for (Person victim : allPersons) {
                this.infect(victim);
            }
        }
        if (this.daysLeftImmune > 0) {
            this.daysLeftImmune = this.daysLeftImmune - 1;
        }
        if (GET_WELL_PROB >= Math.random() && this.isSick) {
            this.isSick = false;
            this.daysLeftImmune = this.DAYS_IMMUNE;
        }
        if (DIE_PROB >= Math.random() && this.isSick) {
            this.isSick = false;
            this.isDead = true;
        }

    }

    public void becomesInfected() {
        if (!this.isDead && this.daysLeftImmune == 0) {
            this.isSick = true;
        }
    }

    public void infect(Person victim) {
        double xDistance = (this.xPos - victim.xPos);
        double yDistance = (this.yPos - victim.yPos);
        double distance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));

        if (INFECT_PROB >= Math.random() && this != victim && this.range > distance) {

            victim.becomesInfected();
        }
    }

}
