/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 The MsgCodec Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cinnober.msgcodec.messages;

import java.util.ArrayList;
import java.util.Collections;

import com.cinnober.msgcodec.GroupDef;
import com.cinnober.msgcodec.NamedType;
import com.cinnober.msgcodec.Schema;
import com.cinnober.msgcodec.anot.Id;
import com.cinnober.msgcodec.anot.Name;
import com.cinnober.msgcodec.anot.Required;
import com.cinnober.msgcodec.anot.Sequence;
import com.cinnober.msgcodec.visitor.FieldDefVisitor;
import com.cinnober.msgcodec.visitor.GroupDefVisitor;
import com.cinnober.msgcodec.visitor.NamedTypeVisitor;
import com.cinnober.msgcodec.visitor.SchemaVisitor;
import java.util.List;

/**
 * Message for a schema.
 *
 * @author mikael.brannstrom
 */
@Name("Schema")
@Id(16000)
public class MetaSchema extends MetaAnnotated {
    /**
     * The groups in the schema.
     */
    @Required
    @Sequence(MetaGroupDef.class)
    @Id(1)
    public List<MetaGroupDef> groups;

    /**
     * The named types in the schema.
     */
    @Sequence(MetaNamedType.class)
    @Id(2)
    public List<MetaNamedType> namedTypes;

    public MetaSchema() {}

    public MetaSchema(List<MetaGroupDef> groups,
            List<MetaNamedType> namedTypes) {
        this.groups = groups;
        this.namedTypes = namedTypes;
    }


    public List<NamedType> toNamedTypes() {
        if (namedTypes == null) {
            return Collections.emptyList();
        }
        List<NamedType> list = new ArrayList<>(namedTypes.size());
        for (MetaNamedType namedType : namedTypes) {
            list.add(new NamedType(namedType.name, namedType.type.toTypeDef(), namedType.toAnnotationsMap()));
        }
        return list;
    }

    public List<GroupDef> toGroupDefs() {
        if (groups == null) {
            return Collections.emptyList();
        }
        List<GroupDef> list = new ArrayList<>(groups.size());
        for (MetaGroupDef group : groups) {
            list.add(group.toGroupDef());
        }
        return list;
    }

    public Schema toSchema() {
        return new Schema(toGroupDefs(), toNamedTypes(), toAnnotationsMap(), null);
    }

    /**
     * Visit this schema with the specified schema visitor.
     * @param sv the schema visitor, not null.
     */
    public void visit(SchemaVisitor sv) {
        visit(this, sv);
    }

    private static void visit(MetaSchema schema, SchemaVisitor sv) {
        sv.visit(null);
        schema.annotations.forEach(a -> sv.visitAnnotation(a.name, a.value));
        schema.namedTypes.forEach(t -> {
            NamedTypeVisitor tv = sv.visitNamedType(t.name, t.type.toTypeDef());
            if (tv != null) {
                t.annotations.forEach(a -> tv.visitAnnotation(a.name, a.value));
                tv.visitEnd();
            }
        });
        schema.groups.forEach(g -> {
            GroupDefVisitor gv = sv.visitGroup(g.name, g.id, g.superGroup, null);
            if (gv != null) {
                g.annotations.forEach(a -> gv.visitAnnotation(a.name, a.value));
                g.fields.forEach(f -> {
                    FieldDefVisitor fv = gv.visitField(f.name, f.id, f.required, f.type.toTypeDef(), null);
                    if (fv != null) {
                        f.annotations.forEach(a -> fv.visitAnnotation(a.name, a.value));
                        fv.visitEnd();
                    }
                });
                gv.visitEnd();
            }
        });
    }

}
