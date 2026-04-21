import json
import pytest
from unittest.mock import MagicMock, patch
from pathlib import Path


@pytest.fixture
def mock_claude_client():
    with patch("converter.claude_client.ClaudeClient.send") as mock_send:
        yield mock_send


def test_load_samples_missing_to_be(tmp_path, monkeypatch):
    """AS-IS에만 파일이 있고 TO-BE가 없으면 해당 샘플을 건너뜀."""
    from converter.analyzer import PatternAnalyzer
    monkeypatch.chdir(tmp_path)
    (tmp_path / "samples" / "as_is").mkdir(parents=True)
    (tmp_path / "samples" / "to_be").mkdir(parents=True)
    (tmp_path / "samples" / "as_is" / "Foo.java").write_text("// EJB code")

    analyzer = PatternAnalyzer()
    pairs = analyzer.load_samples()
    assert pairs == []


def test_load_samples_matched_pair(tmp_path, monkeypatch):
    """AS-IS와 TO-BE 파일이 모두 있으면 쌍으로 로드됨."""
    from converter.analyzer import PatternAnalyzer
    monkeypatch.chdir(tmp_path)
    (tmp_path / "samples" / "as_is").mkdir(parents=True)
    (tmp_path / "samples" / "to_be").mkdir(parents=True)
    (tmp_path / "samples" / "as_is" / "Foo.java").write_text("// EJB")
    (tmp_path / "samples" / "to_be" / "Foo.java").write_text("// Spring")

    analyzer = PatternAnalyzer()
    pairs = analyzer.load_samples()
    assert len(pairs) == 1
    assert pairs[0]["filename"] == "Foo.java"


def test_convert_all_no_input(tmp_path, monkeypatch):
    """input/ 폴더에 파일이 없으면 FileNotFoundError."""
    from converter.converter import EjbConverter
    monkeypatch.chdir(tmp_path)
    (tmp_path / "input").mkdir()
    (tmp_path / "patterns").mkdir()
    patterns = {"annotation_mappings": {}}
    (tmp_path / "patterns" / "learned_patterns.json").write_text(json.dumps(patterns))

    converter = EjbConverter()
    with pytest.raises(FileNotFoundError):
        converter.convert_all()
