/*
 * Copyright 2016 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.ls;

import com.google.common.base.Optional;
import com.palantir.ls.api.CompilerWrapper;
import com.palantir.ls.api.TreeParser;
import com.palantir.ls.api.WorkspaceCompiler;
import io.typefox.lsapi.CompletionList;
import io.typefox.lsapi.FileEvent;
import io.typefox.lsapi.Location;
import io.typefox.lsapi.Position;
import io.typefox.lsapi.PublishDiagnosticsParams;
import io.typefox.lsapi.ReferenceParams;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.TextDocumentContentChangeEvent;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wraps a WorkspaceCompiler and TreeParser. Ensures the tree is updated when the compiler is compiled successfully.
 */
public class DefaultCompilerWrapper implements CompilerWrapper {

    private final WorkspaceCompiler compiler;
    private final TreeParser parser;

    public DefaultCompilerWrapper(WorkspaceCompiler compiler, TreeParser parser) {
        this.compiler = compiler;
        this.parser = parser;
    }

    @Override
    public void parseAllSymbols() {
        parser.parseAllSymbols();
    }

    @Override
    public Map<URI, Set<SymbolInformation>> getFileSymbols() {
        return parser.getFileSymbols();
    }

    @Override
    public CompletionList getCompletion(URI uri, Position position) {
        return parser.getCompletion(uri, position);
    }

    @Override
    public Map<Location, Set<Location>> getReferences() {
        return parser.getReferences();
    }

    @Override
    public Set<Location> findReferences(ReferenceParams params) {
        return parser.findReferences(params);
    }

    @Override
    public Optional<Location> gotoDefinition(URI uri, Position position) {
        return parser.gotoDefinition(uri, position);
    }

    @Override
    public Set<SymbolInformation> getFilteredSymbols(String query) {
        return parser.getFilteredSymbols(query);
    }

    @Override
    public URI getWorkspaceRoot() {
        return compiler.getWorkspaceRoot();
    }

    @Override
    public Set<PublishDiagnosticsParams> compile() {
        Set<PublishDiagnosticsParams> diagnostics = compiler.compile();
        if (diagnostics.isEmpty()) {
            parser.parseAllSymbols();
        }
        return diagnostics;
    }

    @Override
    public void handleFileOpened(URI file) {
        compiler.handleFileOpened(file);
    }

    @Override
    public void handleFileChanged(URI originalFile, List<TextDocumentContentChangeEvent> contentChanges) {
        compiler.handleFileChanged(originalFile, contentChanges);
    }

    @Override
    public void handleFileClosed(URI originalFile) {
        compiler.handleFileClosed(originalFile);
    }

    @Override
    public void handleFileSaved(URI originalFile) {
        compiler.handleFileSaved(originalFile);
    }

    @Override
    public void handleChangeWatchedFiles(List<? extends FileEvent> changes) {
        compiler.handleChangeWatchedFiles(changes);
    }

}
