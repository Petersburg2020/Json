package nx.peter.java.util;

import nx.peter.java.json.Json;
import nx.peter.java.json.core.JsonElement;
import nx.peter.java.pis.Pis;
import nx.peter.java.pis.core.Node;
import nx.peter.java.util.data.Letters;
import nx.peter.java.util.data.Word;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nx.peter.java.util.Util.isString;
import static nx.peter.java.util.Util.tab;
import static nx.peter.java.util.data.DataManager.*;

public class Misc {

    public static String toPrettyAttr(Node.Attrs<Node.Attr> attrs, int indent) {
        return toAttr(attrs, indent, true);
    }

    public static String toAttr(Node.Attrs<Node.Attr> attrs, int indent) {
        return toAttr(attrs, indent, false);
    }

    public static String toAttr(Node.Attrs<Node.Attr> attrs, int indent, boolean format) {
        var word = new Word(attrs.map(attr -> {
            Object value = ((nx.peter.java.pis.reader.Node.Attr) attr).get();
            if (isString(value))
                value = "\"" + value + "\"";
            return (format ? "\n" + tab(indent + 1) : " ") + "\"" + ((nx.peter.java.pis.reader.Node.Attr) attr).getName() + "\"=" + value;
        }).collect(Collectors.joining()));
        return word.remove("{", 0).remove("}", word.lastIndexOf("}") - 1).get();
    }

    public static String toPrettyAttr(Map<String, Object> attrs, int indent) {
        return toAttr(attrs, indent, true);
    }

    public static String toAttr(Map<String, Object> attrs, int indent) {
        return toAttr(attrs, indent, false);
    }

    public static String toAttr(Map<String, Object> attrs, int indent, boolean format) {
        var word = new Word(attrs.entrySet().stream().map(e -> {
            Object value = e.getValue();
            if (isString(value))
                value = "\"" + value + "\"";
            return (format ? "\n" + tab(indent + 1) : " ") + "\"" + e.getKey() + "\"=" + value;
        }).collect(Collectors.joining()));
        return word.remove("{", 0).remove("}", word.lastIndexOf("}") - 1).get();
    }

    public static <N extends Node, A extends Node.Attr> String toPrettyPis(Pis<N, A> pis, int indent) {
        return toPis(pis, indent, true);
    }

    public static <N extends Node, A extends Node.Attr> String toPis(Pis<N, A> pis, int indent) {
        return toPis(pis, indent, false);
    }

    public static <N extends Node, A extends Node.Attr> String toPrettyPis(N pis, int indent) {
        return toPis(pis, indent, true);
    }

    public static <N extends Node, A extends Node.Attr> String toPis(N pis, int indent) {
        return toPis(pis, indent, false);
    }

    public static <N extends Node, A extends Node.Attr> String toPis(N node, int indent, boolean format) {
        String result = (format ? tab(indent) : "") + "<" + node.getTag();
        nx.peter.java.pis.reader.Node nd = (nx.peter.java.pis.reader.Node) node;
        result += (node.hasAttribute() ? "" + (format ? toPrettyAttr(nd.getAttributes(), indent) : toAttr(nd.getAttributes(), indent + 1)) : "") + ">";

        if (nd.isNotEmpty())
            if (nd.isSingleValue())
                result += (format && nd.hasAttribute() ? "\n" + tab(indent + 1) : "") + nd.get();
            else {
                int count = 0;
                for (Object o : nd.getChildren()) {
                    if (count == 0) result += format ? "\n" : "";
                    nx.peter.java.pis.reader.Node n = (nx.peter.java.pis.reader.Node) o;
                    result += format ? toPrettyPis(n, indent + 1) : toPis(n, indent + 1);
                    count++;
                    if (count < nd.getChildren().size())
                        result += format ? "\n" : " ";
                }
            }
        result += (format && (nd.hasChildren() || nd.hasAttribute()) ? "\n" + tab(indent) : "") + "</" + nd.getTag() + ">";
        return result;
    }

    public static <N extends Node, A extends Node.Attr> String toPis(Pis<N, A> pis, int indent, boolean format) {
        String result = "";
        if (pis.isValid()) {
            result += (format ? tab(indent) : "") + "<" + pis.getTag();
            result += (pis.hasAttribute() ? "" + (format ? toPrettyAttr(pis.getAttrs(), indent) : toAttr(pis.getAttrs(), indent + 1)) : "") + ">";

            if (pis.isNotEmpty())
                if (pis.isSingleValue())
                    result += (format && pis.hasAttribute() ? "\n" + tab(indent + 1) : "") + pis.get();
                else {
                    int count = 0;
                    for (N node : pis.getChildren()) {
                        if (count == 0) result += format ? "\n" : "";
                        result += format ? toPrettyPis(node, indent + 1) : toPis(node, indent + 1);
                        count++;
                        if (count < pis.getChildren().size())
                            result += format ? "\n" : " ";
                    }
                }
            result += (format && (pis.hasChildren() || pis.hasAttribute()) ? "\n" + tab(indent) : "") + "</" + pis.getTag() + ">";
        }

        return result;
    }





    public static <JA extends JsonElement, JO extends JsonElement, JE extends JsonElement> Json<JA, JO, JE> extractJson(CharSequence json) {
        if (json != null && json.toString().trim().startsWith("[") && json.toString().trim().endsWith("]"))
            return new Json<>(extractArray(json));
        else if (json != null && json.toString().trim().startsWith("{") && json.toString().trim().endsWith("}"))
            return new Json<>(extractObject(json));
        return new Json<>();
    }

    public static <N extends Node, A extends Node.Attr> Pis<N, A> extractPis(CharSequence pis) {
        NodeMap node = extractNode(pis);
        return new Pis<>(node.tag, node.node);
    }

    public static NodeMap extractNode(CharSequence letters) {
        String tag = "";
        Word pis = new Word(letters != null ? letters.toString().trim() : "");
        List<Pis.TagValue> node = new ArrayList<>();
        int start = 0, end = 0;

        // System.out.println("Extract: \n" + pis.get());
        if (pis.startsWith("<") && pis.contains("</") && pis.getLetterCount('>') > 1 && pis.endsWith(">")) {
            tag = pis.extractWords().isNotEmpty() ? pis.extractWords().get(0).get() : "";
            Word wrap = new Word();

            // System.out.println("Tag: " + tag);

            wrap.set(pis.subLetters("<", start, ">"))
                    .removeAll("\t")
                    .replaceAll("\n", ", ")
                    .remove("<" + tag + ", ")
                    .trim()
					/*.append("\" ", 0)
					.append(" \"")*/;
            System.out.println("Wrap0: " + wrap.get());
            // System.out.println("Wrap Words: " + wrap.getWords());

            // Check if attribute exists
            Letters.Split split = wrap.split(", ", "=");
            System.out.println("Split0: " + split);
            if (split.isNotEmpty()) {
                Map<String, Object> attrs = new LinkedHashMap<>();

                for (String w : split) {
                    Letters.Split attr = new Word(w).split("=");
                    if (attr.size() == 2) {
                        String name = new Word(attr.get(0)).trim().removeAll("\"").get();
                        String val0 = attr.get(1).trim();
                        System.out.println("Type: " + getType(val0));
                        // val0 = val0.contains("\"") ? new Word(val0).remove("\"", val0.length() - 2).remove("\"").get() : val0;
                        Object val = attr.size() > 1 ? toObject(val0) : null;
                        if (val != null) {
                            /*if (val0.contains("\""))
                                val = new Word(val0).remove("\"", val0.length() - 2).remove("\"").get();*/
                            System.out.println("Val: " + val);
                            attrs.put(name, val);
                        }
                    }
                }
                // System.out.println("Attrs: " + attrs);
                // Add all attributes
                node.add(new Pis.TagValue(Pis.ATTRS_NAME, attrs));
            }
            // System.out.println("PIS: " + pis.subLetters(">\n", start, pis.lastIndexOf("</" + tag)));

            wrap.set(pis.subLetters(">\n", start, pis.lastIndexOf("</")));
            if (!pis.contains(">\n"))
                wrap.set(pis.subLetters("> ", start, pis.lastIndexOf("</")));

            wrap.removeAll("\t")
                    .replaceAll("\n", " ")
                    .remove(">")
                    .trim();
            System.out.println("Start1: " + start + ", Wrap1: " + wrap.get());


            if (wrap.contains(">") && wrap.contains("</") && wrap.startsWith("<") && wrap.endsWith(">")) {
                List<String> nodeList = new ArrayList<>();
                int startNode = 0;
                while (wrap.substring(startNode) != null) {
                    String dTag = wrap.subLetters("<", startNode) != null ? wrap.subLetters("<", startNode).get() : null;

                    // System.out.println("DTag: " + dTag);
                    if (dTag == null)
                        break;
                    Letters.Words words = new Word(dTag).getWords();
                    if (words.isNotEmpty()) {
                        Word word = words.get(0).remove("<");
                        dTag = word.contains(">") ? word.subLetters(0, word.indexOf(">")).get() : word.get();
                    } else dTag = null;
                    // dTag = words.isNotEmpty() ? words.get(0).remove("<").subLetters(0, words.get(0).indexOf(">")).get() : null;
                    // System.out.println("DTag: " + dTag);
                    if (dTag == null)
                        break;

                    System.out.println("DTag: " + dTag);
                    startNode += dTag.length();
                    String dNode = wrap.subLetters(">", startNode, wrap.indexOf("</" + dTag, startNode)) != null ? wrap.subLetters(">", startNode, wrap.indexOf("</" + dTag, startNode)).remove("> ").get() : null;
                    if (dNode == null)
                        break;
                    // System.out.println("DNode: " + dNode);
                    startNode = wrap.indexOf(dNode + "</" + dTag + ">", startNode) + (dNode + "</" + dTag + ">").length();
                    nodeList.add("<" + dTag + "> " + dNode + "</" + dTag + ">");

                    // System.out.println("Start node: " + startNode + ", Length: " + wrap.length());
                    if (startNode >= wrap.length() || startNode < 0)
                        break;

                    // Update Child Node
                    /*Pis.TagValue tagValue = extractChildNode("<" + dTag + "> " + dNode + "</" + dTag + ">");
                    System.out.println("Node: " + tagValue.value);
                    node.add(tagValue);*/
                }
                // System.out.println("Node List: " + nodeList);

                // Update Child Nodes
                for (String dNode : nodeList) {
                    NodeMap map = extractChildNode(dNode);
                    // System.out.println("Node: " + map.node);
                    node.add(new Pis.TagValue(map.tag, map.node));
                }
            } else {
                // System.out.println("Value: " + toObject(wrap.remove(">").get()));
                node.add(new Pis.TagValue(Pis.SINGLE_CHILD_TAG, toObject(wrap.get())));
            }
        }
        // System.out.println("Node: " + node);
        return new NodeMap(tag, new Pis.TagValueList(node));
    }

    protected static NodeMap extractChildNode(CharSequence letters) {
        String tag = "";
        Word pis = new Word(letters != null ? letters.toString().trim() : "");
        List<Pis.TagValue> node = new ArrayList<>();
        int start = 0, end = 0;

        // System.out.println("Extract: \n" + pis.get());
        if (pis.startsWith("<") && pis.contains("</") && pis.getLetterCount('>') > 1 && pis.endsWith(">")) {
            tag = pis.extractWords().isNotEmpty() ? pis.extractWords().get(0).get() : "";
            Word wrap = new Word();

            // System.out.println("Tag: " + tag);

            wrap.set(pis.subLetters("<", start, ">"))
                    .removeAll("\t")
                    .replaceAll("\n", ", ")
                    .remove("<" + tag + ", ")
                    .trim()
					/*.append("\" ", 0)
					.append(" \"")*/;
            System.out.println("Wrap01: " + wrap.get());
            // System.out.println("Wrap Words: " + wrap.getWords());

            // Check if attribute exists
            Letters.Split split = wrap.split(", ", "=");
            System.out.println("Split: " + split);
            if (split.isNotEmpty()) {
                Map<String, Object> attrs = new LinkedHashMap<>();

                for (String w : split) {
                    Letters.Split attr = new Word(w).split("=");
                    if (attr.size() == 2) {
                        String name = new Word(attr.get(0)).trim().removeAll("\"").get();
                        String val0 = attr.get(1).trim();
                        System.out.println("Type: " + getType(val0));
                        // val0 = val0.contains("\"") ? new Word(val0).remove("\"", val0.length() - 2).remove("\"").get() : val0;
                        Object val = attr.size() > 1 ? toObject(val0) : null;
                        if (val != null) {
                            /*if (val0.contains("\""))
                                val = new Word(val0).remove("\"", val0.length() - 2).remove("\"").get();*/
                            System.out.println("Val: " + val);
                            attrs.put(name, val);
                        }
                    }
                }
                // System.out.println("Attrs: " + attrs);
                // Add all attributes
                node.add(new Pis.TagValue(Pis.ATTRS_NAME, attrs));
            }
            // System.out.println("PIS: " + pis.subLetters(">\n", start, pis.lastIndexOf("</" + tag)));

            wrap.set(pis.subLetters(">\n", start, pis.lastIndexOf("</")));
            if (!pis.contains(">\n"))
                wrap.set(pis.subLetters("> ", start, pis.lastIndexOf("</")));

            wrap.removeAll("\t")
                    .replaceAll("\n", " ")
                    .remove(">")
                    .trim();
            System.out.println("Start11: " + start + ", Wrap11: " + wrap.get());


            if (wrap.contains(">") && wrap.contains("</") && wrap.startsWith("<") && wrap.endsWith(">")) {
                List<String> nodeList = new ArrayList<>();
                int startNode = 0;
                while (wrap.substring(startNode) != null) {
                    String dTag = wrap.subLetters("<", startNode) != null ? wrap.subLetters("<", startNode).get() : null;

                    // System.out.println("DTag: " + dTag);
                    if (dTag == null)
                        break;
                    Letters.Words words = new Word(dTag).getWords();
                    if (words.isNotEmpty()) {
                        Word word = words.get(0).remove("<");
                        dTag = word.contains(">") ? word.subLetters(0, word.indexOf(">")).get() : word.get();
                    } else dTag = null;
                    // dTag = words.isNotEmpty() ? words.get(0).remove("<").subLetters(0, words.get(0).indexOf(">")).get() : null;
                    // System.out.println("DTag: " + dTag);
                    if (dTag == null)
                        break;

                    System.out.println("DTag: " + dTag);
                    startNode += dTag.length();
                    String dNode = wrap.subLetters(">", startNode, wrap.indexOf("</" + dTag, startNode)) != null ? wrap.subLetters(">", startNode, wrap.indexOf("</" + dTag, startNode)).remove("> ").get() : null;
                    if (dNode == null)
                        break;
                    // System.out.println("DNode: " + dNode);
                    startNode = wrap.indexOf(dNode + "</" + dTag + ">", startNode) + (dNode + "</" + dTag + ">").length();
                    nodeList.add("<" + dTag + "> " + dNode + "</" + dTag + ">");

                    // System.out.println("Start node: " + startNode + ", Length: " + wrap.length());
                    if (startNode >= wrap.length() || startNode < 0)
                        break;

                    // Update Child Node
                    /*Pis.TagValue tagValue = extractChildNode("<" + dTag + "> " + dNode + "</" + dTag + ">");
                    System.out.println("Node: " + tagValue.value);
                    node.add(tagValue);*/
                }
                // System.out.println("Node List: " + nodeList);

                // Update Child Nodes
                for (String dNode : nodeList) {
                    NodeMap map = extractNode(dNode);
                    // System.out.println("Node: " + map.node);
                    node.add(new Pis.TagValue(map.tag, map.node));
                }
            } else {
                // System.out.println("Value: " + toObject(wrap.remove(">").get()));
                node.add(new Pis.TagValue(Pis.SINGLE_CHILD_TAG, toObject(wrap.get())));
            }
        }
        System.out.println("Node: " + node);
        return new NodeMap(tag, new Pis.TagValueList(node));
    }


    public static class NodeMap {
        protected CharSequence tag;
        Pis.TagValueList node;

        public NodeMap(CharSequence tag, Pis.TagValueList node) {
            this.tag = tag;
            this.node = node;
        }

        public CharSequence tag() {
            return tag;
        }

        public Pis.TagValueList node() {
            return node;
        }

    }


}
