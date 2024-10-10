import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.managers.EntitiesAPI;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.HeroItemsAPI;

public class UseHability implements Behavior {

	protected final HeroAPI hero;
	protected final HeroItemsAPI items;

	protected final Collection<? extends Player> players;

	// constructor
	public UseHability(EntitiesAPI entities, HeroAPI hero, HeroItemsAPI items) {
		this.players = entities.getPlayers();
		this.hero = hero;
		this.items = items;
	}

	@Override
	public void onTickBehavior() {
		Player enemy = getMostClosedEnemy();
		if (enemy != null && enemy.distance(hero) < 900) {
			this.items.useItem(SelectableItem.Spray.FIST_BUMP, ItemFlag.USABLE);
		}
	}

	private Player getMostClosedEnemy() {
		return this.players.stream()
				.filter(player -> player.isEnemy())
				.min(Comparator.comparingDouble(player -> player.distance(hero)))
				.orElse(null);
	}

}
