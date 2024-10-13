import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.extensions.Feature;
import eu.darkbot.api.game.group.GroupMember;
import eu.darkbot.api.game.items.SelectableItem;
import eu.darkbot.api.entities.Player;
import eu.darkbot.api.managers.EntitiesAPI;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.HeroItemsAPI;
import eu.darkbot.api.managers.PetAPI;

import java.util.Collection;
import java.util.Optional;

@Feature(name = "LiderMegamina", description = "Sincroniza el uso de MEGA_MINE con el líder del grupo o clan")
public class LiderMegamina implements Module, Listener {

	private final PetAPI petAPI;
	private final HeroAPI heroAPI;
	private final GroupAPI groupAPI;
	private final Collection<? extends Player> players;

	private boolean syncWithGroupLeader;
	// private boolean syncWithClanLeader;

	@Inject
	public LiderMegamina(EntitiesAPI entities, PluginAPI api, PetAPI petAPI, HeroAPI heroAPI, GroupAPI groupAPI) {
		this.players = entities.getPlayers();
		this.petAPI = petAPI;
		this.heroAPI = heroAPI;
		this.groupAPI = groupAPI;
	}

	@Override
	public void onTickModule() {
		GroupMember groupMember = getGroupLeader();
		Player leaderPlayer = convert(groupMember);
		if (leader != null && leader.hasPet() && leader.getPet().isPresent()) {
			Pet leaderPet = leader.getPet().get();
			if (isPlayerPetUsingGear(leaderPlayer, PetGear.MEGA_MINE)) {
				try {
					if (petAPI.hasGear(PetGear.MEGA_MINE)) {
						petAPI.useGear(PetGear.MEGA_MINE);
					}
				} catch (ItemNotEquippedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isPlayerPetUsingGear(Player player, PetGear desiredGear) {
		if (player.hasPet()) {
			Optional<Pet> petOptional = player.getPet();
			if (petOptional.isPresent()) {
				Pet pet = petOptional.get();
				if (pet instanceof PetAPI) {
					PetAPI petAPI = (PetAPI) pet;
					PetGear currentGear = petAPI.getGear();
					return currentGear == desiredGear;
				}
			}
		}
		return false;
	}

	private GroupMember getGroupLeader() {
		for (GroupMember member : groupAPI.getMembers()) {
			if (member.isLeader()) {
				return member;
			}
		}
		return null;
	}

	public Player convert(GroupMember groupMember) {
		MemberInfo memberInfo = groupMember.getMemberInfo();
		String username = memberInfo.getUsername();

		List<Player> players = heroAPI.getVisiblePlayers();
		for (Player player : players) {
			if (player.getUsername().equals(username)) {
				return player;
			}
		}

		// Si no se encuentra, devuelve null o lanza una excepción
		return null;
	}

	@EventHandler
	public void onGroupLeaderChange(GroupAPI.GroupLeaderChangeEvent event) {
		// Handle group leader change if needed
	}

	// @EventHandler
	// public void onClanLeaderChange(ClanAPI.ClanLeaderChangeEvent event) {
	// 	// Handle clan leader change if needed
	// }
}