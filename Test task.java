import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private List<Document> storage = new ArrayList();
    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {

        if(document.getId() == null || document.getId().isEmpty()){

            document.setId(UUID.randomUUID().toString());

            storage.add(document);
        }

        storage.add(document);

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        List<Document> findStorage =  storage.stream()
                .filter(document -> matchesTitlePrefixes(document,request.getTitlePrefixes()))
                .filter(document -> matchesContainsContents(document,request.getContainsContents()))
                .filter(document -> matchesAuthorIds(document,request.getAuthorIds()))
                .filter(document -> matchesCreatedFrom(document,request.getCreatedFrom()))
                .filter(document -> matchesCreatedTo(document,request.getCreatedTo()))
                .collect(Collectors.toList());

        return findStorage;
    }

    private boolean matchesTitlePrefixes(Document doc, List<String> titlePrefixes) {
        if (titlePrefixes == null) return true;
        return titlePrefixes.stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix));
    }

    private boolean matchesContainsContents(Document doc, List<String> containsContents) {
        if (containsContents == null) return true;
        return containsContents.stream().anyMatch(content -> doc.getContent().contains(content));
    }

    private boolean matchesAuthorIds(Document doc, List<String> authorIds) {
        if (authorIds == null) return true;
        return authorIds.contains(doc.getAuthor().getId());
    }

    private boolean matchesCreatedFrom(Document doc, Instant createdFrom) {
        if (createdFrom == null) return true;
        return !doc.getCreated().isBefore(createdFrom);
    }

    private boolean matchesCreatedTo(Document doc, Instant createdTo) {
        if (createdTo == null) return true;
        return !doc.getCreated().isAfter(createdTo);
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */

    public Optional<Document> findById(String id) {

        for (Document doc : storage){
            if(doc.getId().equals(id)){
                return Optional.of(doc);
            }
        }
        return Optional.empty();
    }


    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}