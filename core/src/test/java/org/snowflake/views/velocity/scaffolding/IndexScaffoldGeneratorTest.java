package org.snowflake.views.velocity.scaffolding;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.WebAction;
import org.snowflake.devserver.DevServer;

public class IndexScaffoldGeneratorTest {

    House house;

    @Before
    public void buildHouse() {
        House house = new House();
        house.setId(42);
        house.setName("Dr Evil's private residence");

        Room kitchen = new Room(1, "Kitchen");
        kitchen.walls.add(new Wall(1, "red"));
        kitchen.walls.add(new Wall(2, "blue"));
        Room bathroom = new Room(2, "Bathroom");
        bathroom.walls.add(new Wall(1, "green"));
        bathroom.walls.add(new Wall(2, "black"));
        Room commandCenter = new Room(3, "Command center");
        commandCenter.walls.add(new Wall(1, "red"));
        commandCenter.walls.add(new Wall(2, "pink"));

        this.house = house;
    }

    @Test
    public void testBuildScaffoldTemplate() throws Exception {
        IndexScaffoldGenerator generator = new IndexScaffoldGenerator(new DevServer(""));
        Answer answer = new Answer();
        answer.setAutoGenerated(true);
        answer.setData(Arrays.asList(house));
        answer.getScaffoldHints().columns("name", "rooms");
        answer.getScaffoldHints().addRowAction(new WebAction("/edit", "Edit"));
        answer.getScaffoldHints().addPageAction(new WebAction("/add", "Add"));
        answer.getScaffoldHints().setColumnLink("rooms", "/rooms/edit?houseId=$!entry.Id");

        String html = generator.buildScaffoldTemplate(answer);
        assertTrue(html.contains("<h1>Houses</h1>"));
        assertTrue(html.contains("<table"));
        assertTrue(html.contains("<th>Name</th>"));
        assertTrue(html.contains("<th>Rooms</th>"));
        assertTrue(html.contains("#foreach($entry in $houses)"));
        assertTrue(html.contains("<td>$!entry.Name</td>"));
        assertTrue(html.contains("<td><a href=\"/rooms/edit?houseId=$!entry.Id\">Rooms</a></td>"));
        assertTrue(html.contains("<a href=\"/edit/$!entry.Id\">Edit</a>"));
        assertTrue(html.contains("#end"));
        assertTrue(html.contains("<a href=\"/add\">Add</a>"));
    }

    /**
     * A House has many Rooms which have many Walls
     */
    class House {

        Integer id;

        Set<Room> rooms = new LinkedHashSet<Room>();

        String name;

        public House() {

        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Set<Room> getRooms() {
            return rooms;
        }

        public void setRooms(Set<Room> rooms) {
            this.rooms = rooms;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    class Room {

        int id;

        Set<Wall> walls = new LinkedHashSet<Wall>();

        String name;

        public Room() {

        }

        public Room(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Set<Wall> getWalls() {
            return walls;
        }

        public void setWalls(Set<Wall> walls) {
            this.walls = walls;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // double size;

    }

    class Wall {

        Integer id;

        String color;

        public Wall() {

        }

        public Wall(Integer id, String color) {
            this.id = id;
            this.color = color;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String type) {
            this.color = type;
        }

    }

}
