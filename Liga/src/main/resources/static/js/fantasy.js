$(document).ready(function() {
    // Formation handling
    const formations = {
        '4-4-2': { def: 4, mid: 4, fwd: 2 },
        '4-3-3': { def: 4, mid: 3, fwd: 3 },
        '4-5-1': { def: 4, mid: 5, fwd: 1 },
        '3-5-2': { def: 3, mid: 5, fwd: 2 },
        '3-4-3': { def: 3, mid: 4, fwd: 3 },
        '5-3-2': { def: 5, mid: 3, fwd: 2 },
        '5-4-1': { def: 5, mid: 4, fwd: 1 }
    };

    function updateFormation(formation) {
        const positions = formations[formation];
        
        // Clear existing positions
        $('.defenders, .midfielders, .forwards').empty();
        
        // Add defender positions
        for (let i = 0; i < positions.def; i++) {
            $('.defenders').append('<div class="player-position" data-position="DEF"></div>');
        }
        
        // Add midfielder positions
        for (let i = 0; i < positions.mid; i++) {
            $('.midfielders').append('<div class="player-position" data-position="MID"></div>');
        }
        
        // Add forward positions
        for (let i = 0; i < positions.fwd; i++) {
            $('.forwards').append('<div class="player-position" data-position="FWD"></div>');
        }
        
        // Update formation in backend
        $.post('/fantasy/formation', { formation: formation })
            .fail(function(response) {
                alert('Failed to update formation: ' + response.responseText);
            });
    }

    // Initialize formation
    updateFormation($('#formation').val());

    // Formation change handler
    $('#formation').change(function() {
        updateFormation($(this).val());
    });

    // Filter handlers
    function updatePlayerList() {
        const teamId = $('#teamFilter').val();
        const position = $('#positionFilter').val();
        
        $.get('/fantasy/players', { teamId: teamId, position: position })
            .done(function(players) {
                const container = $('.available-players');
                container.empty();
                
                players.forEach(function(player) {
                    const card = $(`
                        <div class="player-card">
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">${player.name} ${player.surname}</h5>
                                    <p class="card-text">
                                        <span>${player.position}</span> |
                                        <span>${player.team ? player.team.teamName : 'No Team'}</span>
                                    </p>
                                    <p class="card-text">
                                        Price: ${player.price}M
                                    </p>
                                    <button class="btn btn-primary buy-player" data-id="${player.football_player_id}">
                                        Buy Player
                                    </button>
                                </div>
                            </div>
                        </div>
                    `);
                    container.append(card);
                });
            })
            .fail(function() {
                alert('Failed to load players');
            });
    }

    $('#teamFilter, #positionFilter').change(updatePlayerList);

    // Buy player handler
    $(document).on('click', '.buy-player', function() {
        const playerId = $(this).data('id');
        
        $.post('/fantasy/buy/' + playerId)
            .done(function() {
                location.reload(); // Refresh to update all sections
            })
            .fail(function(response) {
                alert('Failed to buy player: ' + response.responseText);
            });
    });

    // Sell player handler
    $(document).on('click', '.sell-player', function() {
        const playerId = $(this).data('id');
        
        $.post('/fantasy/sell/' + playerId)
            .done(function() {
                location.reload(); // Refresh to update all sections
            })
            .fail(function(response) {
                alert('Failed to sell player: ' + response.responseText);
            });
    });

    // Player selection and field placement
    let selectedPlayer = null;

    $(document).on('click', '.select-player', function() {
        const button = $(this);
        const playerId = button.data('id');
        const position = button.data('position');
        
        // Toggle selection
        if (selectedPlayer && selectedPlayer.id === playerId) {
            selectedPlayer = null;
            $('.player-position').removeClass('selectable');
            button.removeClass('active');
        } else {
            // Deselect previous
            $('.select-player').removeClass('active');
            
            // Select new
            selectedPlayer = {
                id: playerId,
                position: position
            };
            button.addClass('active');
            
            // Highlight valid positions
            $('.player-position').removeClass('selectable');
            $('.player-position[data-position="' + position + '"]:not(.occupied)').addClass('selectable');
        }
    });

    $(document).on('click', '.player-position', function() {
        const position = $(this).data('position');
        
        if (selectedPlayer && position === selectedPlayer.position) {
            // Place player in position
            $(this).addClass('occupied').text(selectedPlayer.id);
            
            // Reset selection
            selectedPlayer = null;
            $('.select-player').removeClass('active');
            $('.player-position').removeClass('selectable');
        }
    });
}); 