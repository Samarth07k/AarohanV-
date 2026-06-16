package com.artistlink.opportunity;

import com.artistlink.application.Application;
import com.artistlink.application.ApplicationRepository;
import com.artistlink.auth.AuthPrincipal;
import com.artistlink.common.AuthorType;
import com.artistlink.common.CursorPagination;
import com.artistlink.common.PageResponse;
import com.artistlink.common.exception.BadRequestException;
import com.artistlink.common.exception.ForbiddenException;
import com.artistlink.common.exception.NotFoundException;
import com.artistlink.opportunity.dto.*;
import com.artistlink.venue.Venue;
import com.artistlink.venue.VenueRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OpportunityService {

    private static final int PAGE = 20;

    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final VenueRepository venueRepository;

    public OpportunityService(OpportunityRepository opportunityRepository,
                              ApplicationRepository applicationRepository,
                              VenueRepository venueRepository) {
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.venueRepository = venueRepository;
    }

    /* ---- Venue: create ---- */
    @Transactional
    public OpportunityResponse create(AuthPrincipal actor, CreateOpportunityRequest req) {
        requireVenue(actor);
        if (req.budgetMin() != null && req.budgetMax() != null && req.budgetMin() > req.budgetMax()) {
            throw new BadRequestException("budgetMin cannot exceed budgetMax");
        }
        Opportunity o = new Opportunity();
        o.setVenueId(actor.authorId());
        o.setTitle(req.title());
        o.setDescription(req.description() == null ? "" : req.description());
        o.setEventDate(req.eventDate());
        o.setBudgetMin(req.budgetMin());
        o.setBudgetMax(req.budgetMax());
        o = opportunityRepository.save(o);
        return toResponse(o, actor);
    }

    /* ---- Venue: update / change status ---- */
    @Transactional
    public OpportunityResponse update(AuthPrincipal actor, UUID id, UpdateOpportunityRequest req) {
        Opportunity o = requireOwnedOpportunity(actor, id);
        if (req.title() != null) o.setTitle(req.title());
        if (req.description() != null) o.setDescription(req.description());
        if (req.eventDate() != null) o.setEventDate(req.eventDate());
        if (req.budgetMin() != null) o.setBudgetMin(req.budgetMin());
        if (req.budgetMax() != null) o.setBudgetMax(req.budgetMax());
        if (req.status() != null) o.setStatus(req.status());
        o = opportunityRepository.save(o);
        return toResponse(o, actor);
    }

    @Transactional
    public OpportunityResponse close(AuthPrincipal actor, UUID id) {
        Opportunity o = requireOwnedOpportunity(actor, id);
        o.setStatus(OpportunityStatus.CLOSED);
        o = opportunityRepository.save(o);
        return toResponse(o, actor);
    }

    /* ---- Discovery (artists + anyone): OPEN, cursor paginated ---- */
    @Transactional(readOnly = true)
    public PageResponse<OpportunityResponse> discover(AuthPrincipal actor, String cursor, Integer limit) {
        int size = (limit == null || limit <= 0 || limit > 50) ? PAGE : limit;
        CursorPagination.Cursor c = CursorPagination.decode(cursor);
        List<Opportunity> rows = (c == null)
                ? opportunityRepository.findOpenFirstPage(Limit.of(size + 1))
                : opportunityRepository.findOpenAfter(c.createdAt(), c.id(), Limit.of(size + 1));
        boolean hasMore = rows.size() > size;
        List<Opportunity> page = hasMore ? rows.subList(0, size) : rows;
        List<OpportunityResponse> items = page.stream().map(o -> toResponse(o, actor)).toList();
        String next = null;
        if (hasMore && !page.isEmpty()) {
            Opportunity last = page.get(page.size() - 1);
            next = CursorPagination.encode(last.getCreatedAt(), last.getId());
        }
        return PageResponse.of(items, next);
    }

    /* ---- Venue: my opportunities ---- */
    @Transactional(readOnly = true)
    public List<OpportunityResponse> mine(AuthPrincipal actor) {
        requireVenue(actor);
        return opportunityRepository.findByVenueIdOrderByCreatedAtDesc(actor.authorId())
                .stream().map(o -> toResponse(o, actor)).toList();
    }

    /* ---- Detail ---- */
    @Transactional(readOnly = true)
    public OpportunityResponse get(AuthPrincipal actor, UUID id) {
        Opportunity o = opportunityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
        return toResponse(o, actor);
    }

    /* ---- helpers ---- */
    private OpportunityResponse toResponse(Opportunity o, AuthPrincipal actor) {
        Venue venue = venueRepository.findById(o.getVenueId()).orElse(null);
        String venueName = venue == null ? "Unknown Venue" : venue.getDisplayName();
        String venueLocation = venue == null ? null : venue.getLocation();
        long appCount = applicationRepository.countByOpportunityId(o.getId());

        boolean hasApplied = false;
        UUID myApplicationId = null;
        if (actor != null && actor.authorType() == AuthorType.ARTIST) {
            Optional<Application> mine = applicationRepository
                    .findByOpportunityIdAndArtistId(o.getId(), actor.authorId());
            hasApplied = mine.isPresent();
            myApplicationId = mine.map(Application::getId).orElse(null);
        }
        return OpportunityResponse.of(o, venueName, venueLocation, appCount, hasApplied, myApplicationId);
    }

    private void requireVenue(AuthPrincipal actor) {
        if (actor.authorType() != AuthorType.VENUE) {
            throw new ForbiddenException("Only venues can perform this action");
        }
    }

    private Opportunity requireOwnedOpportunity(AuthPrincipal actor, UUID id) {
        requireVenue(actor);
        Opportunity o = opportunityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Opportunity not found"));
        if (!o.getVenueId().equals(actor.authorId())) {
            throw new ForbiddenException("You do not own this opportunity");
        }
        return o;
    }
}
