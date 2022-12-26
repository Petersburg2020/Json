package nx.peter.java.document;

import nx.peter.java.document.core.Page;
import nx.peter.java.json.reader.JsonArray;
import nx.peter.java.pis.reader.Node;

import java.util.List;

public interface Pages<P extends Page> extends Iterable<P> {
    P getPage(int page);
    List<P> list();
    int size();
    boolean isEmpty();
    boolean isNotEmpty();
    <A extends Page> int indexOf(A page);
    <A extends Page> boolean contains(A page);
    <A extends Page> boolean equals(Pages<A> another);
    <A extends Page> boolean equals(List<A> pages);

    JsonArray<?> toJson();
    Node<?, ?> toPis();
}
