package qatestlab;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {

    private static final Log LOG = new Log();

    public static void main(String[] args) throws Exception {
        Race race1, race2;

        if (new Random().nextInt() % 2 == 0) {
            race1 = Race.ELF;
        } else {
            race1 = Race.PEOPLE;
        }

        if (new Random().nextInt() % 2 == 0) {
            race2 = Race.ORK;
        } else {
            race2 = Race.UNDEAD;
        }

        List<Unit> units1 = new ArrayList<>(Arrays.asList(new Unit(100, Type.MAGE, race1),
                new Unit(100, Type.ARCHER, race1), new Unit(100, Type.ARCHER, race1), new Unit(100, Type.ARCHER, race1),
                new Unit(100, Type.WARRIOR, race1), new Unit(100, Type.WARRIOR, race1), new Unit(100, Type.WARRIOR, race1), new Unit(100, Type.WARRIOR, race1)));

        List<Unit> units2 = new ArrayList<>(Arrays.asList(new Unit(100, Type.MAGE, race2),
                new Unit(100, Type.ARCHER, race2), new Unit(100, Type.ARCHER, race2), new Unit(100, Type.ARCHER, race2),
                new Unit(100, Type.WARRIOR, race2), new Unit(100, Type.WARRIOR, race2), new Unit(100, Type.WARRIOR, race2), new Unit(100, Type.WARRIOR, race2)));

        while (!units1.isEmpty() && !units2.isEmpty()) {
            List<Unit> leftUnits = new ArrayList<>(units1);
            leftUnits.addAll(units2);

            List<Unit> improvedUnits = leftUnits.stream().filter(u -> u.improve).collect(Collectors.toList());
            List<Unit> regularUnits = leftUnits.stream().filter(u -> !u.improve).collect(Collectors.toList());
            while ((!improvedUnits.isEmpty() || !regularUnits.isEmpty())
                    && !units1.isEmpty() && !units2.isEmpty()) {
                final Unit currentUnit;
                if (!improvedUnits.isEmpty()) {
                    currentUnit = improvedUnits.remove(new Random().nextInt(improvedUnits.size()));
                } else {
                    currentUnit = regularUnits.remove(new Random().nextInt(regularUnits.size()));
                }
                if (currentUnit.race == race1) {
                    if (race1 == Race.ELF) {
                        if (currentUnit.type == Type.MAGE) {
                            if (new Random().nextBoolean() || !units1.isEmpty()) {
                                buff(currentUnit, units1, improvedUnits, regularUnits);
                            } else {
                                damage(currentUnit, units2, regularUnits, 10);
                            }
                        }
                        if (currentUnit.type == Type.ARCHER) {
                            if (new Random().nextBoolean()) {
                                damage(currentUnit, units2, regularUnits, 7);
                            } else {
                                damage(currentUnit, units2, regularUnits, 3);
                            }
                        }
                        if (currentUnit.type == Type.WARRIOR) {
                            damage(currentUnit, units2, regularUnits, 15);
                        }
                    }
                    if (race1 == Race.PEOPLE) {
                        if (currentUnit.type == Type.MAGE) {
                            if (new Random().nextBoolean() || !units1.isEmpty()) {
                                buff(currentUnit, units1, improvedUnits, regularUnits);
                            } else {
                                damage(currentUnit, units2, regularUnits, 4);
                            }
                        }
                        if (currentUnit.type == Type.ARCHER) {
                            if (new Random().nextBoolean()) {
                                damage(currentUnit, units2, regularUnits, 5);
                            } else {
                                damage(currentUnit, units2, regularUnits, 3);
                            }
                        }
                        if (currentUnit.type == Type.WARRIOR) {
                            damage(currentUnit, units2, regularUnits, 18);
                        }
                    }
                }
                if (currentUnit.race == race2) {
                    if (race2 == Race.ORK) {
                        if (currentUnit.type == Type.MAGE) {
                            List<Unit> improvedEnemies = units1.stream().filter(u -> u.improve).collect(Collectors.toList());
                            if (!improvedEnemies.isEmpty() && (units1.isEmpty() || new Random().nextBoolean())) {
                                debuff(currentUnit, improvedEnemies);
                            } else if (!units2.isEmpty()) {
                                buff(currentUnit, units2, improvedUnits, regularUnits);
                            }
                        }
                        if (currentUnit.type == Type.ARCHER) {
                            if (new Random().nextBoolean()) {
                                damage(currentUnit, units1, regularUnits, 3);
                            } else {
                                damage(currentUnit, units1, regularUnits, 2);
                            }
                        }
                        if (currentUnit.type == Type.WARRIOR) {
                            damage(currentUnit, units1, regularUnits, 20);
                        }
                    }
                    if (race2 == Race.UNDEAD) {
                        if (currentUnit.type == Type.MAGE) {
                            if (new Random().nextBoolean()) {
                                damage(currentUnit, units1, regularUnits, 3);
                            } else {
                                disease(units1, currentUnit);
                            }
                        }
                        if (currentUnit.type == Type.ARCHER) {
                            if (new Random().nextBoolean()) {
                                damage(currentUnit, units1, regularUnits, 4);
                            } else {
                                damage(currentUnit, units1, regularUnits, 2);
                            }
                        }
                        if (currentUnit.type == Type.WARRIOR) {
                            damage(currentUnit, units1, regularUnits, 18);
                        }
                    }
                }
                currentUnit.disease = false;
                currentUnit.improve = false;
            }
        }
        LOG.saveToFile("results.txt");
    }

    private static void disease(List<Unit> units1, Unit currentUnit) {
        int randomEnemyIndex = new Random().nextInt(units1.size());
        units1.get(randomEnemyIndex).disease = true;
        LOG.log("("
                + currentUnit.race + " " + currentUnit.type + ", "
                + units1.get(randomEnemyIndex).race + " " + units1.get(randomEnemyIndex).type
                + ", disease)");
    }

    private static void debuff(Unit currentUnit, List<Unit> improvedEnemies) {
        Unit enemy = improvedEnemies.get(new Random().nextInt(improvedEnemies.size()));
        enemy.improve = false;
        LOG.log("("
                + currentUnit.race + " " + currentUnit.type + ", "
                + enemy.race + " " + enemy.type
                + ", debuff)");
    }

    private static void buff(Unit currentUnit, List<Unit> allys, List<Unit> improvedUnits, List<Unit> regularUnits) {
        int allyIndex = new Random().nextInt(allys.size());
        Unit ally = allys.get(allyIndex);
        ally.improve = true;
        if (regularUnits.contains(ally)) {
            regularUnits.remove(ally);
            improvedUnits.add(ally);
        }
        LOG.log("("
                + currentUnit.race + " " + currentUnit.type + ", "
                + ally.race + " " + ally.type
                + ", buff)");
    }

    private static void damage(Unit who, List<Unit> enemies, List<Unit> orderedUnits, double baseDamage) {
        int randomEnemyIndex = new Random().nextInt(enemies.size());
        double actualDamage = baseDamage;
        if (who.improve) {
            actualDamage *= 1.5;
        }
        if (who.disease) {
            actualDamage /= 2;
        }

        if (enemies.get(randomEnemyIndex).HP <= actualDamage) {
            enemies.get(randomEnemyIndex).HP = 0;
            LOG.log("(" + who.race + " " + who.type + ", "
                    + enemies.get(randomEnemyIndex).race + " " + enemies.get(randomEnemyIndex).type
                    + ", " + actualDamage + ",  died)");
            orderedUnits.remove(enemies.get(randomEnemyIndex));
            enemies.remove(randomEnemyIndex);
        } else {
            enemies.get(randomEnemyIndex).HP -= actualDamage;
            LOG.log("(" + who.race + " " + who.type + ", "
                    + enemies.get(randomEnemyIndex).race + " " + enemies.get(randomEnemyIndex).type
                    + ", " + actualDamage + ")");
        }
    }

    enum Race {
        ELF, PEOPLE, ORK, UNDEAD
    }

    enum Type {
        MAGE, ARCHER, WARRIOR
    }

    public static class Unit {
        int HP;
        final Type type;
        final Race race;
        boolean disease = false;
        boolean improve = false;

        public Unit(int HP, Type type, Race race) {
            this.HP = HP;
            this.type = type;
            this.race = race;
        }
    }

    private static class Log {

        private StringBuilder log = new StringBuilder();

        public void log(String s) {
            System.out.println(s);
            log.append(s).append("\n");
        }

        public void saveToFile(String filePath) throws IOException {
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(log.toString());
            }
        }
    }
}

