/*
 * Copyright (C) 2016 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Message;

import at.pcgamingfreaks.TestClasses.TestMessage;
import at.pcgamingfreaks.TestClasses.TestMessageComponent;
import at.pcgamingfreaks.TestClasses.TestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class MessageComponentTest
{
	@BeforeClass
	public static void prepareTestData() throws NoSuchFieldException
	{
		TestUtils.initReflection();
	}

	@Test
	public void testGetClassicMessage()
	{
		List<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new TestMessageComponent("test message "));
		messageComponents.add(new TestMessageComponent("with extras", MessageColor.AQUA));
		TestMessageComponent messageComponent = new TestMessageComponent("This is a ");
		messageComponent.setExtras(messageComponents);
		//noinspection SpellCheckingInspection
		assertEquals("The text should match", "This is a test message §r§bwith extras§r§r", messageComponent.getClassicMessage());
		assertEquals("An empty message component should return an empty string", "§r", new TestMessageComponent().getClassicMessage());
		//noinspection SpellCheckingInspection
		assertEquals("The text of the static function should match", "test message §r§bwith extras§r", MessageComponent.getClassicMessage(messageComponents));
		assertEquals("The extras should be found", messageComponents, messageComponent.getExtras());
		assertEquals("An non existent message list should return an empty string", "", MessageComponent.getClassicMessage(null));
	}

	@Test
	public void testFormats()
	{
		TestMessageComponent messageComponent = new TestMessageComponent("This is a text");
		messageComponent.bold().italic().obfuscated().strikethrough().underlined().color(MessageColor.BLACK).insert("Insertion");
		assertTrue("The message should be bold", messageComponent.isBold());
		assertEquals("The message should be black", MessageColor.BLACK, messageComponent.getColor());
		assertEquals("The color of the message should be black", "black", messageComponent.getColorString());
		assertTrue("The message should be italic", messageComponent.isItalic());
		assertTrue("The message should be obfuscated", messageComponent.isObfuscated());
		assertTrue("The message should be strikethrough", messageComponent.isStrikethrough());
		assertTrue("The message should be underlined", messageComponent.isUnderlined());
		assertEquals("The insertion should be trimmed", "Insertion", messageComponent.getInsertion());
		messageComponent.insert("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
		assertEquals("The insertion should be correct", "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789", messageComponent.getInsertion());
		messageComponent.setObfuscated(false);
		messageComponent.setStrikethrough(false);
		assertFalse("The message should not be obfuscated", messageComponent.isObfuscated());
		assertFalse("The message should not be strikethrough", messageComponent.isStrikethrough());
		messageComponent.color("Aqua");
		assertEquals("The message color should be aqua", MessageColor.AQUA, messageComponent.getColor());
		assertEquals("The message text should be equal", "Test text§r", new TestMessageComponent("Test text").getClassicMessage());
		assertEquals("The message text should be equal", "§aTest text§r", new TestMessageComponent("Test text", MessageColor.GREEN).getClassicMessage());
		messageComponent.color(MessageColor.BLUE);
		assertEquals("The color should match the ChatColor", MessageColor.BLUE.toString(), messageComponent.getColor().toString());
		messageComponent.setBold(false);
		assertFalse("The message should not be bold", messageComponent.isBold());
		messageComponent.setItalic(false);
		assertFalse("The message should not be italic", messageComponent.isItalic());
		messageComponent.setUnderlined(false);
		assertFalse("The message should not be underlined", messageComponent.isUnderlined());
		String currentMessage = messageComponent.getClassicMessage();
		messageComponent.addExtra((TestMessageComponent[]) null);
		assertEquals("The message should not have been changed", currentMessage, messageComponent.getClassicMessage());
		messageComponent.addExtra((TestMessageComponent) null);
		assertEquals("The message should not have been changed", currentMessage, messageComponent.getClassicMessage());
		messageComponent.setFormats((MessageFormat[]) null);
		assertEquals("The message should not have been changed", currentMessage, messageComponent.getClassicMessage());
		messageComponent.setFormats();
		assertEquals("The message should not have been changed", currentMessage, messageComponent.getClassicMessage());
		messageComponent.format();
		assertEquals("The message should not have been changed", currentMessage, messageComponent.getClassicMessage());
		messageComponent.format(MessageFormat.BOLD);
		assertTrue("The message should now be bold", messageComponent.isBold());
		messageComponent.format(MessageFormat.ITALIC);
		assertTrue("The message should now be italic", messageComponent.isItalic());
	}

	@Test
	public void testEvents()
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		MessageHoverEvent hoverEvent = new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_ACHIEVEMENT, "achievement");
		messageComponent.setClickEvent(null).setHoverEvent(hoverEvent);
		assertNull("The message should be null", messageComponent.getText());
		assertNull("The click event should be null", messageComponent.getClickEvent());
		assertEquals("The hover event should be returned", hoverEvent, messageComponent.getHoverEvent());
		messageComponent.setHoverEvent(null);
		assertEquals("The JSON of the message should be empty", "{}", messageComponent.toString());
		messageComponent.file("C:\\Test\\Path");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.OPEN_FILE, messageComponent.getClickEvent().getAction());
		assertEquals("The file path should match", "C:\\Test\\Path", messageComponent.getClickEvent().getValue());
		messageComponent.link("http:\\\\test.page.org");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.OPEN_URL, messageComponent.getClickEvent().getAction());
		assertEquals("The link url should match", "http:\\\\test.page.org", messageComponent.getClickEvent().getValue());
		messageComponent.suggest("cmd run");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.SUGGEST_COMMAND, messageComponent.getClickEvent().getAction());
		assertEquals("The given command should be suggested", "cmd run", messageComponent.getClickEvent().getValue());
		messageComponent.command("cmd run");
		assertEquals("The click action should be as given", MessageClickEvent.ClickEventAction.RUN_COMMAND, messageComponent.getClickEvent().getAction());
		assertEquals("The given command should be found", "cmd run", messageComponent.getClickEvent().getValue());
		messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "Text 1");
		assertEquals("The hover message text should match", "Text 1", messageComponent.getHoverEvent().getValue());
		List<TestMessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(messageComponent);
		messageComponent.onHover(MessageHoverEvent.HoverEventAction.SHOW_TEXT, messageComponents);
		assertEquals("The hover message should be the current message object", messageComponents, messageComponent.getHoverEvent().getValue());
		messageComponent.achievementTooltip("Achievement");
		assertEquals("The achievement value should match", "achievement.Achievement", messageComponent.getHoverEvent().getValue());
		messageComponent.statisticTooltip("Statistic");
		assertEquals("The statistic value should match", "stat.Statistic", messageComponent.getHoverEvent().getValue());
		messageComponent.itemTooltip("{}");
		assertEquals("The hover action should match", MessageHoverEvent.HoverEventAction.SHOW_ITEM, messageComponent.getHoverEvent().getAction());
		messageComponent.tooltip("First line", "Second one");
		assertEquals("The hover message text should match", "First line\nSecond one", messageComponent.getHoverEvent().getValue());
		messageComponent.formattedTooltip(new TestMessageComponent("Test text"));
		assertEquals("The hover message text should match the formatted tooltip text", "{\"text\":\"Test text\"}", messageComponent.getHoverEvent().getValue());
		messageComponents.clear();
		messageComponent.formattedTooltip(new TestMessage[0]);
		assertNull("The hover event should be null", messageComponent.getHoverEvent());
		messageComponents.add(new TestMessageComponent("Another text"));
		messageComponent.formattedTooltip(new TestMessage(messageComponents), new TestMessage(messageComponents));
		assertEquals("The hover message text should match the formatted tooltip text", "[{\"text\":\"Another text\"}, {\"text\":\"\\n\"}, {\"text\":\"Another text\"}]", messageComponent.getHoverEvent().getValue().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFormattedTooltipMessageComponentClick()
	{
		new TestMessageComponent().formattedTooltip(new TestMessageComponent().setClickEvent(new MessageClickEvent(MessageClickEvent.ClickEventAction.OPEN_URL, "URL")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFormattedTooltipMessageComponentHover()
	{
		new TestMessageComponent().formattedTooltip(new TestMessageComponent().setHoverEvent(new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "Text")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFormattedTooltipMessageClick()
	{
		List<TestMessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new TestMessageComponent());
		messageComponents.get(0).setClickEvent(new MessageClickEvent(MessageClickEvent.ClickEventAction.OPEN_URL, "URL"));
		new TestMessageComponent().formattedTooltip(new TestMessage(messageComponents));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFormattedTooltipMessageHover()
	{
		List<TestMessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(new TestMessageComponent());
		messageComponents.get(0).setHoverEvent(new MessageHoverEvent(MessageHoverEvent.HoverEventAction.SHOW_TEXT, "Text"));
		new TestMessageComponent().formattedTooltip(new TestMessage(messageComponents));
	}

	@Test
	public void testExtraAndFormat()
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		messageComponent.text("Text");
		assertEquals("The text of the message component should be correct", "Text", messageComponent.getText());
		messageComponent.extra(new TestMessageComponent("another extra"));
		//noinspection SpellCheckingInspection
		assertEquals("The text should match", "Textanother extra§r§r", messageComponent.getClassicMessage());
		messageComponent.addExtra(new TestMessageComponent("Another extra"));
		assertEquals("The message text should equal", "Textanother extra§rAnother extra§r§r", messageComponent.getClassicMessage());
		messageComponent.format(MessageFormat.UNDERLINE, MessageFormat.ITALIC, MessageFormat.BOLD, MessageFormat.STRIKETHROUGH);
		assertTrue("The message should be underlined", messageComponent.isUnderlined());
		assertFalse("The magic flag should not be set", messageComponent.isObfuscated());
		messageComponent.format(MessageFormat.MAGIC);
		messageComponent.color(MessageColor.GREEN);
		assertTrue("The magic flag should now be set", messageComponent.isObfuscated());
		assertEquals("The color of the message should be green", "green", messageComponent.getColorString());
	}

	@Test(expected = NullPointerException.class)
	public void testSetFormatWithError()
	{
		TestMessageComponent messageComponent = new TestMessageComponent("Text");
		messageComponent.setFormats((MessageFormat) null);
	}

	@Test
	public void testFromJsonWorker() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		Method fromJsonWorker = MessageComponent.class.getDeclaredMethod("fromJsonWorker", String.class);
		fromJsonWorker.setAccessible(true);
		//noinspection unchecked
		TestMessage message = new TestMessage((Collection<TestMessageComponent>) fromJsonWorker.invoke(messageComponent, "[[\"St\", \"ring \"], {\"text\":\"from JSON worker\"}, null]"));
		fromJsonWorker.setAccessible(false);
		//noinspection SpellCheckingInspection
		assertEquals("The message text should match", "St§rring §rfrom JSON worker§r", message.getClassicMessage());
	}

	@Test
	public void testFromJsonArrayWorkerWithError() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		TestMessageComponent messageComponent = new TestMessageComponent();
		Method fromJsonWorker = MessageComponent.class.getDeclaredMethod("fromJsonWorker", String.class);
		fromJsonWorker.setAccessible(true);
		MessageComponent.messageComponentConstructor = null;
		//noinspection unchecked
		assertEquals("There should not be returned any message components", 0, ((List<TestMessageComponent>) fromJsonWorker.invoke(messageComponent, "[\"String\"]")).size());
		MessageComponent.messageComponentConstructor = TestMessageComponent.class.getConstructor();
		fromJsonWorker.setAccessible(false);
	}
}