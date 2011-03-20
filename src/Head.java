/*
Copyright 2011 Ostap Cherkashin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.bandilab;

import java.util.*;

final class Head {
    private final Map<String, Integer> index;
    private final Map<String, Class> types;

    Head(Attribute[] attrs) {
        this.index = new LinkedHashMap<String, Integer>();
        this.types = new LinkedHashMap<String, Class>();

        int i = 0;
        for (Attribute a : attrs) {
            this.index.put(a.name, i++);
            this.types.put(a.name, a.type);
        }
    }

    Head project(String ... attrs) {
        Attribute[] h = new Attribute[attrs.length];

        int i = 0;
        for (String a : attrs)
            h[i++] = new Attribute(a, types.get(a));

        return new Head(h);
    }

    Head extend(Extension ... ext) {
        Attribute[] h = new Attribute[index.size() + ext.length];

        int i = 0;
        for (Map.Entry<String, Class> e : types.entrySet())
            h[i++] = new Attribute(e.getKey(), e.getValue());

        for (Extension e : ext) {
            if (index.containsKey(e.attr))
                throw new IllegalArgumentException(
                        "attribute " + e.attr + " already exists");

            h[i++] = new Attribute(e.attr, e.type);
        }

        return new Head(h);
    }

    int size() {
        return index.size();
    }

    int getPos(String attr) {
        Integer res = index.get(attr);
        if (res == null)
            throw new IllegalArgumentException("unknown attribute " + attr);

        return res;
    }

    int[] getIndex(String ... attrs) {
        Collection<String> x = (attrs.length == 0)
            ? index.keySet()
            : Arrays.asList(attrs);

        int i = 0;
        int[] res = new int[x.size()];
        for (String a : x)
            res[i++] = index.get(a); // FIXME: what if not found?

        return res;
    }

    int[] getSortIndex(String ... attrs) {
        int[] res = new int[index.size()];

        Map<String, Integer> idx = new LinkedHashMap<String, Integer>(index);

        int i = 0;
        for (String a : attrs)
            res[i++] = idx.remove(a);

        for (int pos : idx.values())
            res[i++] = pos;

        return res;
    }
}
